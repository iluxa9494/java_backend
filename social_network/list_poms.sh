#!/usr/bin/env bash
set -euo pipefail

ROOT="${1:-.}"
MODE="${2:-list}"  # list | cat

# Исключаем мусорные папки
EXCLUDES=(
  -path "*/.git/*"
  -path "*/target/*"
  -path "*/build/*"
  -path "*/out/*"
  -path "*/.idea/*"
  -path "*/.gradle/*"
  -path "*/node_modules/*"
  -path "*/dist/*"
)

# Собираем список pom.xml (без mapfile)
POMS="$(
  find "$ROOT" \
    \( "${EXCLUDES[@]}" \) -prune -o \
    -type f -name "pom.xml" -print \
  | sort
)"

if [[ -z "${POMS}" ]]; then
  echo "No pom.xml found under: $ROOT"
  exit 0
fi

echo "Found pom.xml:"
echo "${POMS}"

if [[ "$MODE" == "cat" ]]; then
  echo "${POMS}" | while IFS= read -r f; do
    [[ -z "$f" ]] && continue
    echo
    echo "===== FILE: $f ====="
    # первые 240 строк (чтобы не заспамить)
    sed -n '1,240p' "$f"
  done
fi