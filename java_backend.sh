#!/usr/bin/env bash
set -euo pipefail

# ==========================================
# dump_project.sh
# Обходит дерево проекта и сохраняет:
# 1) содержимое текстовых файлов в один .txt
# 2) список пропущенных файлов/папок с причиной — в отдельный .txt
#
# Запуск:
#   chmod +x dump_project.sh
#   ./dump_project.sh
#
# Результат в корне проекта:
#   - project_dump.txt
#   - project_dump_excluded.txt
# ==========================================

# --- где мы должны быть (корень репо) ---
ROOT_DIR="$(pwd)"

OUT_DUMP="${ROOT_DIR}/project_dump.txt"
OUT_EXCL="${ROOT_DIR}/project_dump_excluded.txt"

# --- служебные счетчики ---
included_count=0
excluded_count=0

# --- helper: lower-case (bash 3.2 compatible) ---
lower() {
  # to lowercase, safe for bash 3.2
  printf '%s' "$1" | tr '[:upper:]' '[:lower:]'
}

# --- инициализация файлов вывода ---
: > "$OUT_DUMP"
: > "$OUT_EXCL"

# Заголовки
{
  echo "PROJECT DUMP"
  echo "Root: ${ROOT_DIR}"
  echo "Generated: $(date -u +"%Y-%m-%dT%H:%M:%SZ")"
  echo "=========================================="
  echo
} >> "$OUT_DUMP"

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

# --- список исключаемых директорий (по имени) ---
# Добавляй/убирай по желанию.
EXCLUDE_DIR_NAMES=(
  ".git"
  ".idea"
  ".vscode"
  ".settings"
  ".gradle"
  ".mvn"          # часто содержит wrapper-бинарники/кеши
  "target"
  "build"
  "out"
  "node_modules"
  "dist"
  "coverage"
  ".next"
  ".nuxt"
  ".cache"
  ".terraform"
  ".DS_Store"     # может быть и файлом, и "мусором"
  "__pycache__"
  ".pytest_cache"
  ".mypy_cache"
  ".idea_modules"
  "logs"
  "log"
  "tmp"
  "temp"
  ".tmp"
)

# --- расширения, которые считаем "картинки/медиа/бинарники" ---
EXCLUDE_EXTS=(
  # images
  "png" "jpg" "jpeg" "gif" "bmp" "webp" "tiff" "ico" "svg"
  # media
  "mp3" "wav" "flac" "ogg" "mp4" "mov" "avi" "mkv" "webm"
  # archives
  "zip" "rar" "7z" "tar" "gz" "bz2" "xz"
  # binaries/libs
  "jar" "war" "ear" "class" "exe" "dll" "so" "dylib" "bin"
  # fonts
  "ttf" "otf" "woff" "woff2" "eot"
  # docs/binaries often large
  "pdf" "psd" "ai"
  # db
  "db" "sqlite" "sqlite3"
)

# --- файлы, которые часто “мусор”/временные ---
EXCLUDE_FILE_PATTERNS=(
  "*.swp" "*.swo" "*~"
  ".DS_Store"
  "Thumbs.db"
  "*.iml"
  "*.lock"
  "*.log"
  "*.tmp"
)

# --- helper: записать исключение (путь + причина одним предложением) ---
log_excluded() {
  local rel="$1"
  local reason="$2"
  {
    echo "$rel"
    echo "$reason"
    echo
  } >> "$OUT_EXCL"
  ((excluded_count+=1))
}

# --- helper: безопасно получить относительный путь ---
relpath() {
  local abs="$1"
  # убрать ведущий ROOT_DIR + /
  local rel="${abs#"$ROOT_DIR"/}"
  # если файл прямо в корне
  if [[ "$rel" == "$abs" ]]; then
    rel="$(basename "$abs")"
  fi
  echo "$rel"
}

# --- helper: проверка, является ли путь внутри исключаемой директории ---
is_in_excluded_dir() {
  local p="$1"
  local part
  IFS='/' read -r -a parts <<< "$p"
  for part in "${parts[@]}"; do
    for d in "${EXCLUDE_DIR_NAMES[@]}"; do
      if [[ "$part" == "$d" ]]; then
        return 0
      fi
    done
  done
  return 1
}

# --- helper: проверка по шаблонам файлов ---
matches_exclude_file_pattern() {
  local filename="$1"
  local pat
  for pat in "${EXCLUDE_FILE_PATTERNS[@]}"; do
    # shellcheck disable=SC2053
    if [[ "$filename" == $pat ]]; then
      return 0
    fi
  done
  return 1
}

# --- helper: проверка по расширению ---
has_excluded_ext() {
  local filename="$1"
  local ext="${filename##*.}"
  # если нет точки/расширения
  if [[ "$ext" == "$filename" ]]; then
    return 1
  fi
  ext="$(lower "$ext")"

  local e
  for e in "${EXCLUDE_EXTS[@]}"; do
    if [[ "$ext" == "$e" ]]; then
      return 0
    fi
  done
  return 1
}

# --- helper: является ли файл текстовым (по file(1)) ---
is_text_file() {
  local f="$1"
  local t
  t="$(file -b "$f" 2>/dev/null || true)"
  t="$(lower "$t")"

  if [[ "$t" == *"text"* ]] || [[ "$t" == *"xml"* ]] || [[ "$t" == *"json"* ]] || [[ "$t" == *"yaml"* ]] || [[ "$t" == *"yml"* ]] || [[ "$t" == *"script"* ]] || [[ "$t" == *"ascii"* ]] || [[ "$t" == *"utf-8"* ]]; then
    return 0
  fi
  return 1
}

# --- основной обход ---
# Используем find, но фильтруем сами — так проще и надежнее логировать причины.
while IFS= read -r -d '' f; do
  # пропускаем, если это не обычный файл
  if [[ ! -f "$f" ]]; then
    continue
  fi

  rel="$(relpath "$f")"

  # 1) исключаем по директориям
  if is_in_excluded_dir "$rel"; then
    log_excluded "$rel" "Пропущено, потому что находится в каталоге сборки/IDE/кэшей/логов или другом служебном каталоге."
    continue
  fi

  # 2) исключаем по файловым паттернам
  base="$(basename "$rel")"
  if matches_exclude_file_pattern "$base"; then
    log_excluded "$rel" "Пропущено, потому что это временный/мусорный файл (swap/lock/log/tmp/системный)."
    continue
  fi

  # 3) исключаем по расширениям (картинки/бинарники/архивы и т.д.)
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

  # 4) дополнительно проверяем file(1) чтобы не захватить бинарник без расширения
  if ! is_text_file "$f"; then
    log_excluded "$rel" "Пропущено, потому что файл определяется как не-текстовый (вероятно бинарный)."
    continue
  fi

  # 5) пишем содержимое в общий дамп
  {
    echo "------------------------------------------"
    echo "FILE: $rel"
    echo "SIZE: $(wc -c < "$f" | tr -d ' ') bytes"
    echo "------------------------------------------"
    cat "$f"
    echo
    echo
  } >> "$OUT_DUMP"

  ((included_count+=1))
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
} | tee -a "$OUT_DUMP" >/dev/null

echo "OK: written '$OUT_DUMP' and '$OUT_EXCL'"