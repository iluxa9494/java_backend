#!/usr/bin/env bash
set -euo pipefail

# ------------------------------------------------------------
# run_all_services.sh
# Универсальная версия (macOS bash 3.2 compatible)
# ------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

RESET_DB="${RESET_DB:-0}"
NO_CACHE="${NO_CACHE:-0}"
DETACH="${DETACH:-1}"
FOLLOW_LOGS="${FOLLOW_LOGS:-0}"
STOP_ON_FAIL="${STOP_ON_FAIL:-0}"
TARGETS="${TARGETS:-}"

info() { echo "ℹ️  $*"; }
ok()   { echo "✅ $*"; }
warn() { echo "⚠️  $*"; }
err()  { echo "❌ $*"; }

# Порядок запуска
SERVICE_ORDER=(
  currency_exchange
  hotel_booking
  search_engine
  social_network
  tariff_calculator
  tg_bot
  vk_insight
)

should_run() {
  local key="$1"
  [[ -z "$TARGETS" ]] && return 0

  local norm="${TARGETS//,/ }"
  for t in $norm; do
    [[ "$t" == "$key" ]] && return 0
  done
  return 1
}

service_script_path() {
  case "$1" in
    currency_exchange) echo "currency_exchange/run_currency_exchange_service.sh" ;;
    hotel_booking)     echo "hotel_booking/run_hotel_booking_service.sh" ;;
    search_engine)     echo "search_engine/run_searchengine_service.sh" ;;
    social_network)    echo "social_network/run_social_network_service.sh" ;;
    tariff_calculator)echo "tariff_calculator/run_tariff_calculator.sh" ;;
    tg_bot)            echo "tg_bot/run_tg_bot_service.sh" ;;
    vk_insight)        echo "vk_insight/run_vk_insight_service.sh" ;;
    *)                 echo "" ;;
  esac
}

run_one() {
  local key="$1"
  local script_rel
  script_rel="$(service_script_path "$key")"

  if [[ -z "$script_rel" ]]; then
    err "Неизвестный сервис: $key"
    return 2
  fi

  local script_path="$SCRIPT_DIR/$script_rel"

  if [[ ! -f "$script_path" ]]; then
    err "Скрипт не найден: $script_path"
    return 2
  fi

  if [[ ! -x "$script_path" ]]; then
    warn "chmod +x $script_rel"
    chmod +x "$script_path"
  fi

  info "Запускаю: $key"

  (
    export RESET_DB="$RESET_DB"
    export NO_CACHE="$NO_CACHE"
    export DETACH="$DETACH"
    export FOLLOW_LOGS="$FOLLOW_LOGS"

    cd "$(dirname "$script_path")"
    "./$(basename "$script_path")"
  )
}

echo "📍 Repo root: $SCRIPT_DIR"
echo "🔧 RESET_DB=$RESET_DB NO_CACHE=$NO_CACHE DETACH=$DETACH FOLLOW_LOGS=$FOLLOW_LOGS STOP_ON_FAIL=$STOP_ON_FAIL"
[[ -n "$TARGETS" ]] && echo "🎯 TARGETS=$TARGETS"
echo

failed=()
skipped=()

for key in "${SERVICE_ORDER[@]}"; do
  if ! should_run "$key"; then
    skipped+=("$key")
    continue
  fi

  set +e
  run_one "$key"
  code=$?
  set -e

  if [[ $code -ne 0 ]]; then
    failed+=("$key")
    err "Сервис '$key' упал (code=$code)"
    [[ "$STOP_ON_FAIL" == "1" ]] && break
  else
    ok "Сервис '$key' запущен"
  fi

  echo
done

[[ ${#skipped[@]} -gt 0 ]] && info "Пропущены: ${skipped[*]}"

if [[ ${#failed[@]} -gt 0 ]]; then
  err "Упали сервисы: ${failed[*]}"
  exit 1
fi

ok "Все выбранные сервисы запущены"
exit 0