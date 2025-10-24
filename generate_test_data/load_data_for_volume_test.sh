#!/usr/bin/env bash
set -euo pipefail

basedir="$(dirname "$(realpath "$0")")"
export basedir

seq 0 100 | xargs -n1 -P10 -I{} bash -lc '
  csv="$basedir/csv/ydb/t_remainsapp_funds_movement_{}.csv"
  if [[ -f "$csv" ]]; then
    echo "[PID $$] Импорт: $csv"
    ydb --ca-file ca.crt import file csv --path tpcc/t_remainsapp_funds_movement --null-value "\\N" --columns c_document_id,c_funds_movement_number,c_funds_movement_status,c_funds_direction,c_account_id,c_amount_currency,c_created,c_changed "$csv"
  else
    echo "[PID $$] Пропуск (нет файла): $csv"
  fi
'
