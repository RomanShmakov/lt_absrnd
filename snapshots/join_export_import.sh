#!/bin/bash

# Инициализация переменных по умолчанию
DASHBOARD_NAME=""
SNAPSHOT_NAME=""
FROM=""
TO=""

# Парсим флаги
while getopts "d:s:f:t:" opt; do
  case $opt in
    d) DASHBOARD_NAME="$OPTARG" ;;  # значение после -d
    s) SNAPSHOT_NAME="$OPTARG" ;;  # значение после -s
    f) FROM="$OPTARG" ;;  # значение после -f
    t) TO="$OPTARG" ;;  # значение после -t
    *) 
    #   echo "Использование: $0 -d <DASHBOARD_NAME> -s <SNAPSHOT_NAME> -f <FROM> -t <TO>"
      exit 1
      ;;
  esac
done

# Проверяем обязательные параметры
if [[ -z "$DASHBOARD_NAME" || -z "$SNAPSHOT_NAME" || -z "$FROM" || -z "$TO" ]]; then
  echo "Ошибка: не все обязательные параметры заданы!"
  echo "Использование: $0 -d <DASHBOARD_NAME> -s <SNAPSHOT_NAME> -f <FROM> -t <TO>"
  exit 1
fi

echo ">>> Выполняю экспорт и импорт..."

grafana-snapshots -d "$DASHBOARD_NAME" -s "$SNAPSHOT_NAME" -f "$FROM" -t "$TO" export

grafana-snapshots -i "./archive_snapshots/$SNAPSHOT_NAME.json" import

echo "Готово ✅"
