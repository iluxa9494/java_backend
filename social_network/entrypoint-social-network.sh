#!/usr/bin/env bash
set -euo pipefail

log() {
  echo "[$(date +'%H:%M:%S')] $*"
}

SN_CONFIG_DIR="${SN_CONFIG_DIR:-/config/social_network}"
APP_ENV_FILE="${SN_CONFIG_DIR}/social-network/.env"

JAVA_XMX="${JAVA_XMX:-}"
JAVA_XMS="${JAVA_XMS:-${JAVA_XMX}}"
JAVA_MAX_METASPACE="${JAVA_MAX_METASPACE:-}"
JAVA_GC_OPTS="${JAVA_GC_OPTS:--XX:+UseG1GC -XX:MaxGCPauseMillis=200}"
JAVA_TOOL_OPTIONS_BASE="${JAVA_TOOL_OPTIONS_BASE:-}"

if [[ -z "${JAVA_TOOL_OPTIONS:-}" ]]; then
  JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS_BASE}"
  if [[ -n "${JAVA_XMS}" ]]; then
    JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS} -Xms${JAVA_XMS}"
  fi
  if [[ -n "${JAVA_XMX}" ]]; then
    JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS} -Xmx${JAVA_XMX}"
  fi
  if [[ -n "${JAVA_MAX_METASPACE}" ]]; then
    JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS} -XX:MaxMetaspaceSize=${JAVA_MAX_METASPACE}"
  fi
  JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS} ${JAVA_GC_OPTS}"
fi
export JAVA_TOOL_OPTIONS

read_env_file() {
  local file="$1"
  local -n out="$2"
  out=()

  if [[ -f "${file}" ]]; then
    while IFS= read -r line || [[ -n "${line}" ]]; do
      line="${line%%$'\r'}"
      [[ -z "${line}" || "${line}" == \#* ]] && continue
      out+=("${line}")
    done < "${file}"
  fi
}

log "Starting social-network monolith"

env_args=()
read_env_file "${APP_ENV_FILE}" env_args

exec env "${env_args[@]}" java -jar /app/social-network.jar
