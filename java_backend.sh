#!/usr/bin/env bash
set -euo pipefail

# --- где мы должны быть (корень репо) ---
ROOT_DIR="$(pwd)"

OUT_DUMP="${ROOT_DIR}/java_backend_dump.txt"
OUT_EXCL="${ROOT_DIR}/java_backend_dump_excluded.txt"

# временный output (пишем сюда, в конце mv -> OUT_DUMP) чтобы не словить самозацикливание
TMP_DUMP="${ROOT_DIR}/.java_backend_dump.tmp"

# принудительно включаемые файлы (даже если иначе были бы исключены)
FORCE_INCLUDE=(
  "${ROOT_DIR}/.gitignore"
  "${ROOT_DIR}/Dockerfile"
  "${ROOT_DIR}/entrypoint-java-backend.sh"
)

included_count=0
excluded_count=0

lower() { printf '%s' "$1" | tr '[:upper:]' '[:lower:]'; }

: > "$TMP_DUMP"
: > "$OUT_EXCL"

{
  echo "JAVA_BACKEND DUMP"
  echo "Root: ${ROOT_DIR}"
  echo "Generated: $(date -u +"%Y-%m-%dT%H:%M:%SZ")"
  echo "=========================================="
  echo
} >> "$TMP_DUMP"

{
  echo "EXCLUDED PATHS"
  echo "Root: ${ROOT_DIR}"
  echo "Generated: $(date -u +"%Y-%m-%dT%H:%M:%SZ")"
  echo "------------------------------------------"
  echo "Формат:"
  echo "<путь>"
  echo "<причина одним предложением>"
  echo "------------------------------------------"
  echo
} >> "$OUT_EXCL"

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

log_excluded() {
  local rel="$1"
  local reason="$2"
  {
    echo "$rel"
    echo "$reason"
    echo
  } >> "$OUT_EXCL"
  excluded_count=$((excluded_count + 1))
}

relpath() {
  local abs="$1"
  local rel="${abs#"$ROOT_DIR"/}"
  [[ "$rel" == "$abs" ]] && rel="$(basename "$abs")"
  echo "$rel"
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

is_force_include() {
  local abs="$1"
  local fi
  for fi in "${FORCE_INCLUDE[@]}"; do
    [[ "$abs" == "$fi" ]] && return 0
  done
  return 1
}

# --- основной обход ---
while IFS= read -r -d '' f; do
  [[ -f "$f" ]] || continue

  rel="$(relpath "$f")"
  base="$(basename "$rel")"

  # 0) анти-зацикливание: никогда не читаем output-файлы (и временный тоже)
  if [[ "$f" == "$OUT_DUMP" || "$f" == "$OUT_EXCL" || "$f" == "$TMP_DUMP" ]]; then
    log_excluded "$rel" "Пропущено, потому что это файл результата дампа (защита от самозацикливания)."
    continue
  fi

  # Если файл в FORCE_INCLUDE — пропускаем проверки исключений (кроме output-ов выше)
  if is_force_include "$f"; then
    {
      echo "------------------------------------------"
      echo "FILE: $rel"
      echo "SIZE: $(wc -c < "$f" | tr -d ' ') bytes"
      echo "------------------------------------------"
      cat "$f"
      echo
      echo
    } >> "$TMP_DUMP"
    included_count=$((included_count + 1))
    continue
  fi

  # 1) исключаем по директориям
  if is_in_excluded_dir "$rel"; then
    log_excluded "$rel" "Пропущено, потому что находится в каталоге сборки/IDE/кэшей/логов или другом служебном каталоге."
    continue
  fi

  # 2) исключаем по файловым паттернам
  if matches_exclude_file_pattern "$base"; then
    log_excluded "$rel" "Пропущено, потому что это временный/мусорный файл (swap/lock/log/tmp/системный)."
    continue
  fi

  # 3) исключаем по расширениям
  if has_excluded_ext "$base"; then
    ext="$(lower "${base##*.}")"
    case "$ext" in
      png|jpg|jpeg|gif|bmp|webp|tiff|ico|svg)
        log_excluded "$rel" "Пропущено, потому что это изображение/ассет и не является исходным текстом."
        ;;
      jar|war|ear|class|exe|dll|so|dylib|bin)
        log_excluded "$rel" "Пропущено, потому что это бинарный артефакт (сборка/библиотека) и не нужен в дампе исходников."
        ;;
      zip|rar|7z|tar|gz|bz2|xz)
        log_excluded "$rel" "Пропущено, потому что это архив/упаковка и не читается как текст."
        ;;
      *)
        log_excluded "$rel" "Пропущено, потому что это бинарный/медиа-файл и не является исходным текстом."
        ;;
    esac
    continue
  fi

  # 4) проверяем file(1)
  if ! is_text_file "$f"; then
    log_excluded "$rel" "Пропущено, потому что файл определяется как не-текстовый (вероятно бинарный)."
    continue
  fi

  # 5) пишем содержимое
  {
    echo "------------------------------------------"
    echo "FILE: $rel"
    echo "SIZE: $(wc -c < "$f" | tr -d ' ') bytes"
    echo "------------------------------------------"
    cat "$f"
    echo
    echo
  } >> "$TMP_DUMP"

  included_count=$((included_count + 1))
done < <(find "$ROOT_DIR" -type f -print0)

# --- финальный итог ---
{
  echo "=========================================="
  echo "DONE"
  echo "Included files: $included_count"
  echo "Excluded entries: $excluded_count"
  echo "Dump file: $(basename "$OUT_DUMP")"
  echo "Excluded file: $(basename "$OUT_EXCL")"
  echo "=========================================="
} | tee -a "$TMP_DUMP" >/dev/null

# атомарно заменяем итоговый файл
mv -f "$TMP_DUMP" "$OUT_DUMP"

echo "OK: written '$OUT_DUMP' and '$OUT_EXCL'"