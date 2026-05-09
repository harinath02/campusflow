#!/usr/bin/env bash
set -euo pipefail

component="${1:?component is required}"
log_glob="${2:?log glob is required}"
workspace="${GITHUB_WORKSPACE:-$(pwd)}"
summary_file="${GITHUB_STEP_SUMMARY:-/dev/null}"

shopt -s nullglob
logs=( $log_glob )

escape_annotation() {
  local value="$1"
  value="${value//'%'/'%25'}"
  value="${value//$'\r'/'%0D'}"
  value="${value//$'\n'/'%0A'}"
  printf '%s' "$value"
}

normalize_file() {
  local file="$1"
  local normalized_workspace="${workspace//\\//}"

  file="${file//\\//}"
  file="${file#./}"
  file="${file#$normalized_workspace/}"

  if [[ "$component" == "frontend" && "$file" != frontend/* && -e "$workspace/frontend/$file" ]]; then
    file="frontend/$file"
  fi

  if [[ "$component" == "backend" && "$file" != backend/* && -e "$workspace/backend/$file" ]]; then
    file="backend/$file"
  fi

  printf '%s' "$file"
}

find_java_source() {
  local class_name="$1"
  local found=""

  found="$(find "$workspace/backend/src" -name "${class_name}.java" -print -quit 2>/dev/null || true)"
  if [[ -n "$found" ]]; then
    normalize_file "$found"
  fi

  return 0
}

emit_error() {
  local file="$1"
  local line="$2"
  local column="$3"
  local message="$4"

  file="$(normalize_file "$file")"
  message="$(escape_annotation "$message")"

  if [[ -n "$file" ]]; then
    printf '::error file=%s,line=%s,col=%s::%s\n' "$file" "$line" "$column" "$message"
  else
    printf '::error::%s\n' "$message"
  fi
}

if [[ ${#logs[@]} -eq 0 ]]; then
  {
    printf '## %s checks failed\n\n' "${component^}"
    printf 'No command log files were created before the job failed.\n'
  } >> "$summary_file"
  exit 0
fi

error_lines_file="$(mktemp)"
annotation_count=0

for log in "${logs[@]}"; do
  pending_frontend_message=""

  while IFS= read -r line || [[ -n "$line" ]]; do
    if [[ "$component" == "backend" ]]; then
      if [[ "$line" =~ ^\[ERROR\]\ (.+\.java):\[([0-9]+),([0-9]+)\]\ (.+)$ ]]; then
        emit_error "${BASH_REMATCH[1]}" "${BASH_REMATCH[2]}" "${BASH_REMATCH[3]}" "${BASH_REMATCH[4]}"
        annotation_count=$((annotation_count + 1))
      elif [[ "$line" =~ ^\[ERROR\][[:space:]]+([A-Za-z0-9_]+)\.[^:]+:([0-9]+)[[:space:]]+(.+)$ ]]; then
        source_file="$(find_java_source "${BASH_REMATCH[1]}")"
        if [[ -n "$source_file" ]]; then
          emit_error "$source_file" "${BASH_REMATCH[2]}" 1 "${BASH_REMATCH[3]}"
          annotation_count=$((annotation_count + 1))
        fi
      fi
    fi

    if [[ "$component" == "frontend" ]]; then
      if [[ "$line" =~ ^Error:\ ([^:]+\.(ts|tsx|js|jsx|html|scss|css)):([0-9]+):([0-9]+)\ -\ error\ ([^:]+):\ (.+)$ ]]; then
        emit_error "frontend/${BASH_REMATCH[1]}" "${BASH_REMATCH[3]}" "${BASH_REMATCH[4]}" "${BASH_REMATCH[5]}: ${BASH_REMATCH[6]}"
        annotation_count=$((annotation_count + 1))
      elif [[ "$line" =~ ^([^[:space:]].*\.(ts|tsx|js|jsx))\(([0-9]+),([0-9]+)\):\ error\ (TS[0-9]+):\ (.+)$ ]]; then
        emit_error "frontend/${BASH_REMATCH[1]}" "${BASH_REMATCH[3]}" "${BASH_REMATCH[4]}" "${BASH_REMATCH[5]}: ${BASH_REMATCH[6]}"
        annotation_count=$((annotation_count + 1))
      elif [[ "$line" =~ \[ERROR\][[:space:]]+(.+) ]]; then
        pending_frontend_message="${BASH_REMATCH[1]}"
      elif [[ "$line" =~ ^(AssertionError|TypeError|ReferenceError|Error):[[:space:]]*(.+)$ ]]; then
        pending_frontend_message="${BASH_REMATCH[1]}: ${BASH_REMATCH[2]}"
      elif [[ -n "$pending_frontend_message" && "$line" =~ ^[[:space:]]+([^[:space:]:]+\.(ts|tsx|js|jsx|html|scss|css)):([0-9]+):([0-9]+): ]]; then
        emit_error "frontend/${BASH_REMATCH[1]}" "${BASH_REMATCH[3]}" "${BASH_REMATCH[4]}" "$pending_frontend_message"
        annotation_count=$((annotation_count + 1))
        pending_frontend_message=""
      elif [[ -n "$pending_frontend_message" && "$line" =~ ^([^[:space:]]+\.(scss|css))[[:space:]]+([0-9]+):([0-9]+)[[:space:]] ]]; then
        emit_error "frontend/${BASH_REMATCH[1]}" "${BASH_REMATCH[3]}" "${BASH_REMATCH[4]}" "$pending_frontend_message"
        annotation_count=$((annotation_count + 1))
        pending_frontend_message=""
      elif [[ -n "$pending_frontend_message" && "$line" =~ ^[[:space:]]*[^[:alnum:][:space:]][[:space:]]+([^[:space:]]+\.(ts|tsx|js|jsx|html)):([0-9]+):([0-9]+) ]]; then
        emit_error "frontend/${BASH_REMATCH[1]}" "${BASH_REMATCH[3]}" "${BASH_REMATCH[4]}" "$pending_frontend_message"
        annotation_count=$((annotation_count + 1))
        pending_frontend_message=""
      elif [[ -n "$pending_frontend_message" && "$line" =~ ^[[:space:]]*at[[:space:]]+([^[:space:]]+\.(ts|tsx|js|jsx|html)):([0-9]+):([0-9]+) ]]; then
        emit_error "frontend/${BASH_REMATCH[1]}" "${BASH_REMATCH[3]}" "${BASH_REMATCH[4]}" "$pending_frontend_message"
        annotation_count=$((annotation_count + 1))
        pending_frontend_message=""
      elif [[ -n "$pending_frontend_message" && "$line" =~ ^.*\(([^()]+\.(ts|tsx|js|jsx|html)):([0-9]+):([0-9]+)\)$ ]]; then
        emit_error "frontend/${BASH_REMATCH[1]}" "${BASH_REMATCH[3]}" "${BASH_REMATCH[4]}" "$pending_frontend_message"
        annotation_count=$((annotation_count + 1))
        pending_frontend_message=""
      fi
    fi

    if [[ "$line" =~ \[ERROR\]|\[FAIL\]|[[:space:]]ERROR[[:space:]]|ERR!|^Error:|AssertionError|TypeError|ReferenceError|failed|Failed|FAIL ]]; then
      printf '%s\n' "$line" >> "$error_lines_file"
    fi
  done < "$log"
done

{
  printf '## %s checks failed\n\n' "${component^}"
  printf 'Generated %s file-level error annotation(s). Open the **Annotations** area in this check for clickable file and line links.\n\n' "$annotation_count"
  printf 'Full command logs are uploaded as the `%s-failure-logs` artifact.\n\n' "$component"

  if [[ -s "$error_lines_file" ]]; then
    printf '### Error Lines\n\n'
    printf '```text\n'
    cat "$error_lines_file"
    printf '```\n\n'
  fi

  printf '### Last Log Lines\n\n'
  printf '```text\n'
  for log in "${logs[@]}"; do
    printf '%s\n' "----- $(basename "$log") -----"
    tail -n 120 "$log"
  done
  printf '```\n'
} >> "$summary_file"
