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
hints_file="$(mktemp)"
annotation_count=0

add_hint() {
  local hint="$1"

  if ! grep -Fxq "$hint" "$hints_file"; then
    printf '%s\n' "$hint" >> "$hints_file"
  fi
}

for log in "${logs[@]}"; do
  pending_frontend_message=""

  while IFS= read -r line || [[ -n "$line" ]]; do
    if [[ "$component" == "backend" ]]; then
      if [[ "$line" =~ ^\[ERROR\]\ (.+\.java):\[([0-9]+),([0-9]+)\]\ (.+)$ ]]; then
        message="${BASH_REMATCH[4]}"
        emit_error "${BASH_REMATCH[1]}" "${BASH_REMATCH[2]}" "${BASH_REMATCH[3]}" "$message Hint: open this file and fix the Java compiler error shown here."
        annotation_count=$((annotation_count + 1))
        add_hint "Java compiler error: open the annotated `.java` file and fix the symbol, type, syntax, or import shown in the message."
      elif [[ "$line" =~ ^\[ERROR\][[:space:]]+([A-Za-z0-9_]+)\.[^:]+:([0-9]+)[[:space:]]+(.+)$ ]]; then
        source_file="$(find_java_source "${BASH_REMATCH[1]}")"
        if [[ -n "$source_file" ]]; then
          emit_error "$source_file" "${BASH_REMATCH[2]}" 1 "${BASH_REMATCH[3]} Hint: inspect this failing test location and the stack trace above it."
          annotation_count=$((annotation_count + 1))
          add_hint "Backend test failure: inspect the annotated test/source line and the stack trace in `backend-tests.log`."
        fi
      elif [[ "$line" == *"Unable to find main class"* ]]; then
        emit_error "backend/src/main/java/com/campusflow/Application.java" 1 "Spring Boot could not find the application main class. Hint: restore an active public static void main(String[] args) that calls SpringApplication.run(Application.class, args); do not leave it commented out."
        annotation_count=$((annotation_count + 1))
        add_hint "Backend packaging error: Spring Boot cannot repackage the JAR without an active main method in `Application.java`."
      elif [[ "$line" == *"Failed to execute goal org.springframework.boot:spring-boot-maven-plugin"* ]]; then
        add_hint "Spring Boot Maven plugin failed during packaging. Check the exact plugin message above; for `Unable to find main class`, restore the `main` method in `Application.java`."
      elif [[ "$line" == *"Failed to execute goal"* ]]; then
        add_hint "Maven goal failed. Check the first `[ERROR]` line above this message; it usually contains the real cause."
      fi
    fi

    if [[ "$component" == "frontend" ]]; then
      if [[ "$line" =~ ^Error:\ ([^:]+\.(ts|tsx|js|jsx|html|scss|css)):([0-9]+):([0-9]+)\ -\ error\ ([^:]+):\ (.+)$ ]]; then
        emit_error "frontend/${BASH_REMATCH[1]}" "${BASH_REMATCH[3]}" "${BASH_REMATCH[4]}" "${BASH_REMATCH[5]}: ${BASH_REMATCH[6]} Hint: fix the Angular/TypeScript error at this location."
        annotation_count=$((annotation_count + 1))
        add_hint "Frontend compiler error: open the annotated TypeScript/template/style file and fix the reported Angular/TypeScript error."
      elif [[ "$line" =~ ^([^[:space:]].*\.(ts|tsx|js|jsx))\(([0-9]+),([0-9]+)\):\ error\ (TS[0-9]+):\ (.+)$ ]]; then
        emit_error "frontend/${BASH_REMATCH[1]}" "${BASH_REMATCH[3]}" "${BASH_REMATCH[4]}" "${BASH_REMATCH[5]}: ${BASH_REMATCH[6]} Hint: fix this TypeScript error before rebuilding."
        annotation_count=$((annotation_count + 1))
        add_hint "TypeScript error: open the annotated file and fix the reported `TS...` diagnostic."
      elif [[ "$line" == *"bootstrapApplication"* || "$line" == *"main.ts"* && "$line" == *"error"* ]]; then
        add_hint "Frontend startup error: `frontend/src/main.ts` should actively call `bootstrapApplication(AppComponent, appConfig)`."
      elif [[ "$line" =~ \[ERROR\][[:space:]]+(.+) ]]; then
        pending_frontend_message="${BASH_REMATCH[1]}"
      elif [[ "$line" =~ ^(AssertionError|TypeError|ReferenceError|Error):[[:space:]]*(.+)$ ]]; then
        pending_frontend_message="${BASH_REMATCH[1]}: ${BASH_REMATCH[2]}"
      elif [[ -n "$pending_frontend_message" && "$line" =~ ^[[:space:]]+([^[:space:]:]+\.(ts|tsx|js|jsx|html|scss|css)):([0-9]+):([0-9]+): ]]; then
        emit_error "frontend/${BASH_REMATCH[1]}" "${BASH_REMATCH[3]}" "${BASH_REMATCH[4]}" "$pending_frontend_message Hint: fix the error at this location."
        annotation_count=$((annotation_count + 1))
        add_hint "Frontend build/test error: open the annotated file and fix the failing line."
        pending_frontend_message=""
      elif [[ -n "$pending_frontend_message" && "$line" =~ ^([^[:space:]]+\.(scss|css))[[:space:]]+([0-9]+):([0-9]+)[[:space:]] ]]; then
        emit_error "frontend/${BASH_REMATCH[1]}" "${BASH_REMATCH[3]}" "${BASH_REMATCH[4]}" "$pending_frontend_message Hint: fix the style error at this location."
        annotation_count=$((annotation_count + 1))
        add_hint "Style build error: open the annotated SCSS/CSS file and fix the syntax or import issue."
        pending_frontend_message=""
      elif [[ -n "$pending_frontend_message" && "$line" =~ ^[[:space:]]*[^[:alnum:][:space:]][[:space:]]+([^[:space:]]+\.(ts|tsx|js|jsx|html)):([0-9]+):([0-9]+) ]]; then
        emit_error "frontend/${BASH_REMATCH[1]}" "${BASH_REMATCH[3]}" "${BASH_REMATCH[4]}" "$pending_frontend_message Hint: fix the failing test or source line."
        annotation_count=$((annotation_count + 1))
        add_hint "Vitest failure: open the annotated file and inspect the failed assertion or thrown error."
        pending_frontend_message=""
      elif [[ -n "$pending_frontend_message" && "$line" =~ ^[[:space:]]*at[[:space:]]+([^[:space:]]+\.(ts|tsx|js|jsx|html)):([0-9]+):([0-9]+) ]]; then
        emit_error "frontend/${BASH_REMATCH[1]}" "${BASH_REMATCH[3]}" "${BASH_REMATCH[4]}" "$pending_frontend_message Hint: fix the failing test or source line."
        annotation_count=$((annotation_count + 1))
        add_hint "Vitest failure: open the annotated file and inspect the failed assertion or thrown error."
        pending_frontend_message=""
      elif [[ -n "$pending_frontend_message" && "$line" =~ ^.*\(([^()]+\.(ts|tsx|js|jsx|html)):([0-9]+):([0-9]+)\)$ ]]; then
        emit_error "frontend/${BASH_REMATCH[1]}" "${BASH_REMATCH[3]}" "${BASH_REMATCH[4]}" "$pending_frontend_message Hint: fix the failing test or source line."
        annotation_count=$((annotation_count + 1))
        add_hint "Vitest failure: open the annotated file and inspect the failed assertion or thrown error."
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

  if [[ -s "$hints_file" ]]; then
    printf '### Fix Hints\n\n'
    while IFS= read -r hint; do
      printf '- %s\n' "$hint"
    done < "$hints_file"
    printf '\n'
  fi

  printf '### Last Log Lines\n\n'
  printf '```text\n'
  for log in "${logs[@]}"; do
    printf '%s\n' "----- $(basename "$log") -----"
    tail -n 120 "$log"
  done
  printf '```\n'
} >> "$summary_file"
