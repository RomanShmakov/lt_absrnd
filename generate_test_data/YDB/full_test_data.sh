export basedir=`dirname "$(realpath $0)"`/..
export basedir=`realpath $basedir`


#puthon3 $basedir/static_csv/add_current_date_in_last_column_csv.py


# пересоздать таблицы

# ydb --ca-file $basedir/ca.crt yql --file ydb_reset.sql


# загрузить статические данные

# current_time=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
# sed -i -e "s/replaceThisTimestamp/$current_time/g" "$basedir/static_csv/t_branch.csv"
# sed -i -e "s/replaceThisTimestamp/$current_time/g" "$basedir/static_csv/t_ccy.csv"
# sed -i -e "s/replaceThisTimestamp/$current_time/g" "$basedir/static_csv/t_ccy_rate.csv"
# sed -i -e "s/replaceThisTimestamp/$current_time/g" "$basedir/static_csv/t_filial.csv"
# sed -i -e "s/replaceThisTimestamp/$current_time/g" "$basedir/static_csv/t_parameter.csv" 
# ydb --ca-file $basedir/ca.crt import file csv --path main/t_branch --columns c_branch_id,c_filial,c_is_new_account,c_is_balance,c_name,c_city,c_address,c_changed $basedir/static_csv/t_branch.csv
# ydb --ca-file $basedir/ca.crt import file csv --path main/t_ccy --columns c_ccy,c_description,c_number,c_group,c_currency_edit_field,c_minor_units,c_changed $basedir/static_csv/t_ccy.csv
# ydb --ca-file $basedir/ca.crt import file csv --path main/t_ccy_rate --columns c_ccy,c_date,c_rate,c_changed $basedir/static_csv/t_ccy_rate.csv
# ydb --ca-file $basedir/ca.crt import file csv --path main/t_filial --columns c_filial_id,c_bic,c_regno,c_description,c_date_current,c_changed $basedir/static_csv/t_filial.csv
# # Импортировать данные
# ydb --ca-file $basedir/ca.crt import file csv --path main/t_parameter --columns c_key,c_value,c_description,c_changed $basedir/static_csv/t_parameter.csv
# # Добавить асинхронный индекс
# ydb --ca-file $basedir/ca.crt yql -s 'ALTER TABLE `main/t_parameter` ADD INDEX idx_parameters_changed GLOBAL ASYNC ON (c_changed);'


# Очистить таблицы для генерации тестовых данных (оставить счета ФТ)
# main/t_account
ydb --ca-file $basedir/ca.crt yql -s 'DELETE FROM `main/t_account` where c_account_number not in ("01234567890123456789", "11234567890123456789", "01234567590123466819");'
# remains/t_balance
ydb --ca-file $basedir/ca.crt yql -s 'DELETE FROM `remains/t_balance` where c_account_id not in (SELECT c_account_id FROM `main/t_account` where c_account_number in ("01234567890123456789", "11234567890123456789", "01234567590123466819"));'

# Генерируем тестовые данные

python3 $basedir/generate_data.py --dbtype=ydb --accounts 10000 --docs 1

# Загружаем тестовые данные

# ydb --ca-file $basedir/ca.crt import file csv --path main/t_posting --null-value "\N" --columns c_document_id,c_posting_number,c_date,c_posting_balance_date,c_account_id,c_funds_direction,c_amount,c_amount_base,c_created $basedir/csv/ydb/t_posting.csv 

# remains/t_balance
ydb --ca-file $basedir/ca.crt import file csv --path remains/t_balance --null-value "\N" --columns c_account_id,c_balance_type,c_amount,c_changed $basedir/csv/ydb/t_balance.csv

# Удалить асинхронный индекс
# ydb --ca-file $basedir/ca.crt yql -s 'ALTER TABLE `main/t_account` DROP INDEX idx_account_number;'

# Импортировать данные
# main/t_account
# ydb --ca-file $basedir/ca.crt import file csv --path main/t_account --null-value "\N" --columns c_account_id,c_customer_id,c_account_number,c_ccy,c_filial_id,c_open_date,c_is_active,c_is_blocked_debit,c_is_blocked_credit,c_is_blocked,c_balance_check,c_changed $basedir/csv/ydb/t_account.csv 

# Добавить асинхронный индекс
# ydb --ca-file $basedir/ca.crt yql -s 'ALTER TABLE `main/t_account` ADD INDEX idx_account_number GLOBAL SYNC ON (c_account_number);'

# journals/t_remainsapp_funds_movement
ydb --ca-file $basedir/ca.crt import file csv --path journals/t_remainsapp_funds_movement --null-value "\N" --columns c_document_id,c_funds_movement_number,c_funds_movement_status,c_funds_direction,c_account_id,c_amount_currency,c_created,c_changed $basedir/csv/ydb/t_remainsapp_funds_movement.csv

# Вытаскиваем номера счетов в файлик для jmeter
python3 $basedir/extract_accounts.py
