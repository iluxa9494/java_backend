#!/usr/bin/env bash
set -euo pipefail

# --- КОРНЕВАЯ ПАПКА ДЛЯ ОБХОДА (ТОЛЬКО ОНА) ---
ROOT_DIR="/Users/ilia/IdeaProjects/pet_projects/java_backend/currency_exchange"

# --- КУДА ПИСАТЬ РЕЗУЛЬТАТЫ ---
OUT_INCLUDED="${ROOT_DIR}/currency_exchange_included.txt"
OUT_EXCLUDED="${ROOT_DIR}/currency_exchange_excluded.txt"

# временный output для included (анти-зацикливание)
TMP_INCLUDED="${ROOT_DIR}/.currency_exchange_included.tmp"

included_count=0
excluded_count=0

lower() { printf '%s' "$1" | tr '[:upper:]' '[:lower:]'; }

: > "$TMP_INCLUDED"
: > "$OUT_EXCLUDED"

{
  echo "CURRENCY_EXCHANGE INCLUDED DUMP"
  echo "Root: ${ROOT_DIR}"
  echo "Generated: $(date -u +"%Y-%m-%dT%H:%M:%SZ")"
  echo "=========================================="
  echo
} >> "$TMP_INCLUDED"

{
  echo "CURRENCY_EXCHANGE EXCLUDED PATHS"
  echo "Root: ${ROOT_DIR}"
  echo "Generated: $(date -u +"%Y-%m-%dT%H:%M:%SZ")"
  echo "------------------------------------------"
  echo "FORMAT:"
  echo "PATH: <relative path>"
  echo "REASON: <one sentence>"
  echo "------------------------------------------"
  echo
} >> "$OUT_EXCLUDED"

# --- ИСКЛЮЧЕНИЯ ---
EXCLUDE_DIR_NAMES=(
  ".git" ".idea" ".vscode" ".settings" ".gradle" ".mvn"
  "target" "build" "out"
  "node_modules" "dist" "coverage" ".next" ".nuxt" ".cache"
  ".terraform" "__pycache__" ".pytest_cache" ".mypy_cache"
  "logs" "log" "tmp" "temp" ".tmp"
)

EXCLUDE_EXTS=(
  png jpg jpeg gif bmp webp tiff ico svg
  mp3 wav flac ogg mp4 mov avi mkv webm
  zip rar 7z tar gz bz2 xz
  jar war ear class exe dll so dylib bin
  ttf otf woff woff2 eot
  pdf psd ai
  db sqlite sqlite3
)

EXCLUDE_FILE_PATTERNS=(
  "*.swp" "*.swo" "*~"
  ".DS_Store" "Thumbs.db"
  "*.iml" "*.lock" "*.log" "*.tmp"
)

# --- ВСПОМОГАТЕЛЬНЫЕ ---
relpath() {
  local abs="$1"
  local rel="${abs#"$ROOT_DIR"/}"
  [[ "$rel" == "$abs" ]] && rel="$(basename "$abs")"
  echo "$rel"
}

log_excluded() {
  local rel="$1"
  local reason="$2"
  {
    echo "PATH: $rel"
    echo "REASON: $reason"
    echo
  } >> "$OUT_EXCLUDED"
  excluded_count=$((excluded_count + 1))
}

is_in_excluded_dir() {
  local p="$1"
  local part d
  IFS='/' read -r -a parts <<< "$p"
  for part in "${parts[@]}"; do
    for d in "${EXCLUDE_DIR_NAMES[@]}"; do
      [[ "$part" == "$d" ]] && return 0
    done
  done
  return 1
}

matches_exclude_file_pattern() {
  local filename="$1"
  local pat
  for pat in "${EXCLUDE_FILE_PATTERNS[@]}"; do
    # shellcheck disable=SC2053
    [[ "$filename" == $pat ]] && return 0
  done
  return 1
}

has_excluded_ext() {
  local filename="$1"
  local ext="${filename##*.}"
  [[ "$ext" == "$filename" ]] && return 1
  ext="$(lower "$ext")"
  local e
  for e in "${EXCLUDE_EXTS[@]}"; do
    [[ "$ext" == "$e" ]] && return 0
  done
  return 1
}

is_text_file() {
  local f="$1"
  local t
  t="$(file -b "$f" 2>/dev/null || true)"
  t="$(lower "$t")"
  if [[ "$t" == *"text"* ]] || [[ "$t" == *"xml"* ]] || [[ "$t" == *"json"* ]] || \
     [[ "$t" == *"yaml"* ]] || [[ "$t" == *"yml"* ]] || [[ "$t" == *"script"* ]] || \
     [[ "$t" == *"ascii"* ]] || [[ "$t" == *"utf-8"* ]]; then
    return 0
  fi
  return 1
}

# --- ОСНОВНОЙ ОБХОД (ТОЛЬКО ROOT_DIR) ---
while IFS= read -r -d '' f; do
  [[ -f "$f" ]] || continue

  rel="$(relpath "$f")"
  base="$(basename "$rel")"

  # 0) анти-зацикливание: не читаем собственные файлы результата/временный
  if [[ "$f" == "$OUT_INCLUDED" || "$f" == "$OUT_EXCLUDED" || "$f" == "$TMP_INCLUDED" ]]; then
    log_excluded "$rel" "Skipped because it is an output dump file (self-recursion protection)."
    continue
  fi

  # 1) исключаем по директориям
  if is_in_excluded_dir "$rel"; then
    log_excluded "$rel" "Skipped because it is inside build/IDE/cache/logs or other service directory."
    continue
  fi

  # 2) исключаем по паттернам
  if matches_exclude_file_pattern "$base"; then
    log_excluded "$rel" "Skipped because it matches a temp/junk pattern (swap/lock/log/tmp/system)."
    continue
  fi

  # 3) исключаем по расширениям
  if has_excluded_ext "$base"; then
    ext="$(lower "${base##*.}")"
    case "$ext" in
      png|jpg|jpeg|gif|bmp|webp|tiff|ico|svg)
        log_excluded "$rel" "Skipped because it is an image/asset, not source text."
        ;;
      jar|war|ear|class|exe|dll|so|dylib|bin)
        log_excluded "$rel" "Skipped because it is a binary artifact (build/library), not source text."
        ;;
      zip|rar|7z|tar|gz|bz2|xz)
        log_excluded "$rel" "Skipped because it is an archive and not readable as text."
        ;;
      *)
        log_excluded "$rel" "Skipped because it is a binary/media file and not source text."
        ;;
    esac
    continue
  fi

  # 4) проверяем file(1) на текстовость
  if ! is_text_file "$f"; then
    log_excluded "$rel" "Skipped because file(1) classifies it as non-text (likely binary)."
    continue
  fi

  # 5) пишем включённое: путь + содержимое
  {
    echo "------------------------------------------"
    echo "PATH: $rel"
    echo "SIZE: $(wc -c < "$f" | tr -d ' ') bytes"
    echo "CONTENT:"
    echo "------------------------------------------"
    cat "$f"
    echo
    echo
  } >> "$TMP_INCLUDED"

  included_count=$((included_count + 1))
done < <(find "$ROOT_DIR" -type f -print0)

# --- ФИНАЛЬНЫЙ ИТОГ ---
{
  echo "=========================================="
  echo "DONE"
  echo "Included files: $included_count"
  echo "Excluded entries: $excluded_count"
  echo "Included dump file: $(basename "$OUT_INCLUDED")"
  echo "Excluded list file: $(basename "$OUT_EXCLUDED")"
  echo "=========================================="
} >> "$TMP_INCLUDED"

mv -f "$TMP_INCLUDED" "$OUT_INCLUDED"

echo "OK: written '$OUT_INCLUDED' and '$OUT_EXCLUDED'"