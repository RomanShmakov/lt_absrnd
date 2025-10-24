#!/bin/bash

while true; do
    # Выполняем YQL-запрос и сохраняем вывод
    result=$(ydb --ca-file ~/ca_dev.crt yql -s 'SELECT count(c_status) FROM `journals/t_mainapp` where c_status = "WAITING";' 2>&1)
    
    # Получаем текущее время для timestamp
    timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    # Выводим результат с временной меткой
    echo "[$timestamp] Результат запроса:"
    echo "$result"
    echo "----------------------------------------"
    
    # Ждем 60 секунд перед следующей итерацией
    sleep 60
done