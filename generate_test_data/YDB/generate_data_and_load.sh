export basedir=`dirname "$(realpath $0)"`/..
export basedir=`realpath $basedir`

python3 $basedir/generate_data.py --dbtype=ydb --accounts $1 --docs $2 && \

ydb --ca-file $basedir/ca.crt import file csv --path main/t_posting --null-value "\N" --columns c_document_id,c_posting_number,c_date,c_posting_balance_date,c_account_id,c_funds_direction,c_amount,c_amount_base,c_created $basedir/csv/ydb/t_posting.csv && \
# ydb --ca-file $basedir/ca.crt import file csv --path remains/t_balance --null-value "\N" --columns c_account_id,c_balance_type,c_amount,c_changed $basedir/csv/ydb/t_balance.csv && \
# Удалить асинхронный индекс
# ydb --ca-file $basedir/ca.crt yql -s 'ALTER TABLE `main/t_account` DROP INDEX idx_account_number;'
# Импортировать данные
# ydb --ca-file $basedir/ca.crt import file csv --path main/t_account --null-value "\N" --columns c_account_id,c_customer_id,c_account_number,c_ccy,c_filial_id,c_open_date,c_is_active,c_is_blocked_debit,c_is_blocked_credit,c_is_blocked,c_balance_check,c_changed $basedir/csv/ydb/t_account.csv && \
# Добавить асинхронный индекс
# ydb --ca-file $basedir/ca.crt yql -s 'ALTER TABLE `main/t_account` ADD INDEX idx_account_number GLOBAL SYNC ON (c_account_number);'
