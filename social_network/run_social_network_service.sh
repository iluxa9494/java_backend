#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="${ROOT_DIR}/docker-compose.yml"
PROJECT_NAME="${PROJECT_NAME:-social_network}"

log() { echo "[$(date +'%H:%M:%S')] $*"; }

ensure_docker() {
  if ! docker info >/dev/null 2>&1; then
    echo "[ERROR] Docker недоступен. Запусти Docker Desktop (или docker service) и повтори."
    exit 1
  fi
}

ensure_compose_file() {
  if [[ ! -f "${COMPOSE_FILE}" ]]; then
    echo "[ERROR] Не найден ${COMPOSE_FILE}"
    exit 1
  fi
}

dc() {
  docker compose -p "${PROJECT_NAME}" -f "${COMPOSE_FILE}" "$@"
}

up() {
  ensure_docker
  ensure_compose_file
  log "UP (build+detach): ${PROJECT_NAME}"
  dc up -d --build
  log "DONE"
}

down() {
  ensure_docker
  ensure_compose_file
  log "DOWN: ${PROJECT_NAME}"
  dc down
  log "STOPPED"
}

down_volumes() {
  ensure_docker
  ensure_compose_file
  log "DOWN + REMOVE VOLUMES: ${PROJECT_NAME}"
  dc down -v
  log "STOPPED (volumes removed)"
}

ps() {
  ensure_docker
  ensure_compose_file
  dc ps
}

logs_all() {
  ensure_docker
  ensure_compose_file
  # По умолчанию — хвост логов, чтобы не утонуть
  dc logs -f --tail=200
}

logs_one() {
  ensure_docker
  ensure_compose_file
  local svc="${1:?Usage: $0 logs <service>}"
  dc logs -f --tail=300 "${svc}"
}

restart() {
  ensure_docker
  ensure_compose_file
  log "RESTART: ${PROJECT_NAME}"
  dc restart
  log "RESTARTED"
}

rebuild() {
  ensure_docker
  ensure_compose_file
  log "REBUILD (no-cache) + UP: ${PROJECT_NAME}"
  dc build --no-cache
  dc up -d
  log "DONE"
}

help() {
  cat <<EOF
Usage: $0 <command>

Commands:
  up                Build and start all services (detached)
  down              Stop and remove containers (keep volumes)
  clean             Stop and remove containers + volumes (FULL reset)
  ps                Show status
  logs              Follow logs for all services (tail=200)
  logs <service>    Follow logs for one service (tail=300)
  restart           Restart all services
  rebuild           Rebuild all images with --no-cache and start

Env:
  PROJECT_NAME      Compose project name (default: social_network)

Examples:
  $0 up
  $0 logs
  $0 logs kafka
  $0 clean && $0 up
EOF
}

cmd="${1:-up}"
case "${cmd}" in
  up) up ;;
  down) down ;;
  clean) down_volumes ;;
  ps) ps ;;
  logs)
    if [[ "${2:-}" == "" ]]; then
      logs_all
    else
      logs_one "${2}"
    fi
    ;;
  restart) restart ;;
  rebuild) rebuild ;;
  -h|--help|help) help ;;
  *)
    echo "[ERROR] Unknown command: ${cmd}"
    help
    exit 1
    ;;
esac