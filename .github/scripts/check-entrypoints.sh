#!/usr/bin/env bash
set -euo pipefail

workspace="${GITHUB_WORKSPACE:-$(pwd)}"
target="${1:-all}"
failed=0
details_file="$(mktemp)"

escape_annotation() {
  local value="$1"
  value="${value//'%'/'%25'}"
  value="${value//$'\r'/'%0D'}"
  value="${value//$'\n'/'%0A'}"
  printf '%s' "$value"
}

emit_error() {
  local file="$1"
  local line="$2"
  local message="$3"

  printf '::error file=%s,line=%s::%s\n' "$file" "$line" "$(escape_annotation "$message")"
  printf '%s|%s|%s\n' "$file" "$line" "$message" >> "$details_file"

  printf '\n'
  printf '%s\n' 'CI ERROR DETAIL'
  printf '%s\n' '---------------'
  printf 'File: %s\n' "$file"
  printf 'Line: %s\n' "$line"
  printf 'Message: %s\n' "$message"
  printf '\n'
}

find_uncommented_line() {
  local file="$1"
  local pattern="$2"

  awk -v pattern="$pattern" '
    $0 ~ pattern && $0 !~ /^[[:space:]]*\/\// {
      print NR
      exit
    }
  ' "$file"
}

find_any_line() {
  local file="$1"
  local pattern="$2"

  awk -v pattern="$pattern" '
    $0 ~ pattern {
      print NR
      exit
    }
  ' "$file"
}

backend_file="backend/src/main/java/com/campusflow/Application.java"
backend_path="$workspace/$backend_file"

if [[ "$target" == "all" || "$target" == "backend" ]]; then
if [[ -f "$backend_path" ]]; then
  spring_line="$(find_uncommented_line "$backend_path" "@SpringBootApplication")"
  main_line="$(find_uncommented_line "$backend_path" "public[[:space:]]+static[[:space:]]+void[[:space:]]+main[[:space:]]*[(]")"
  run_line="$(find_uncommented_line "$backend_path" "SpringApplication[.]run[[:space:]]*[(]")"
  commented_main_line="$(find_any_line "$backend_path" "public[[:space:]]+static[[:space:]]+void[[:space:]]+main[[:space:]]*[(]")"

  if [[ -z "$spring_line" ]]; then
    emit_error "$backend_file" 1 "Missing active @SpringBootApplication annotation. Hint: keep @SpringBootApplication on the Application class so Spring Boot can discover the app."
    failed=1
  fi

  if [[ -z "$main_line" || -z "$run_line" ]]; then
    emit_error "$backend_file" "${commented_main_line:-1}" "Spring Boot main method is missing or commented out. This causes Maven package to fail with 'Unable to find main class'. Hint: restore public static void main(String[] args) { SpringApplication.run(Application.class, args); }."
    failed=1
  fi
else
  emit_error "$backend_file" 1 "Backend entry point file is missing. Hint: create Application.java with @SpringBootApplication and a public static void main method."
  failed=1
fi
fi

frontend_file="frontend/src/main.ts"
frontend_path="$workspace/$frontend_file"

if [[ "$target" == "all" || "$target" == "frontend" ]]; then
if [[ -f "$frontend_path" ]]; then
  bootstrap_line="$(find_uncommented_line "$frontend_path" "bootstrapApplication[[:space:]]*[(]")"
  commented_bootstrap_line="$(find_any_line "$frontend_path" "bootstrapApplication[[:space:]]*[(]")"

  if [[ -z "$bootstrap_line" ]]; then
    emit_error "$frontend_file" "${commented_bootstrap_line:-1}" "Angular bootstrapApplication call is missing or commented out. The app may compile but will not start correctly. Hint: restore bootstrapApplication(AppComponent, appConfig).catch((err) => console.error(err));."
    failed=1
  fi
else
  emit_error "$frontend_file" 1 "Frontend entry point file is missing. Hint: create main.ts and bootstrap AppComponent with bootstrapApplication."
  failed=1
fi
fi

if [[ "$failed" -ne 0 ]]; then
  {
    printf '## Entrypoint checks failed\n\n'
    printf 'The workflow found startup-code problems before running the heavier build commands.\n\n'
    printf '### Exact Error Locations\n\n'
    printf '| File | Line | Error and Fix Hint |\n'
    printf '| --- | ---: | --- |\n'
    while IFS='|' read -r file line message; do
      message="${message//|/ }"
      printf '| `%s` | %s | %s |\n' "$file" "$line" "$message"
    done < "$details_file"
    printf '\n'
    printf '### Hints\n\n'
    if [[ "$target" == "all" || "$target" == "backend" ]]; then
      printf '%s\n' '- Backend: `backend/src/main/java/com/campusflow/Application.java` must contain an active `public static void main(String[] args)` calling `SpringApplication.run(Application.class, args);`.'
    fi
    if [[ "$target" == "all" || "$target" == "frontend" ]]; then
      printf '%s\n' '- Frontend: `frontend/src/main.ts` must contain an active `bootstrapApplication(AppComponent, appConfig).catch((err) => console.error(err));` call.'
    fi
  } >> "${GITHUB_STEP_SUMMARY:-/dev/null}"

  exit 1
fi
