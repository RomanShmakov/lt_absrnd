export basedir=`dirname "$(realpath $0)"`/..
export basedir=`realpath $basedir`


#puthon3 $basedir/static_csv/add_current_date_in_last_column_csv.py && \

# current_time=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
# sed -i -e "s/replaceThisTimestamp/$current_time/g" "$basedir/static_csv/t_branch.csv"
# sed -i -e "s/replaceThisTimestamp/$current_time/g" "$basedir/static_csv/t_ccy.csv"
# sed -i -e "s/replaceThisTimestamp/$current_time/g" "$basedir/static_csv/t_ccy_rate.csv"
# sed -i -e "s/replaceThisTimestamp/$current_time/g" "$basedir/static_csv/t_filial.csv"
# sed -i -e "s/replaceThisTimestamp/$current_time/g" "$basedir/static_csv/t_parameter.csv"
# ydb --ca-file $basedir/ca.crt import file csv --path main/t_branch --columns c_branch_id,c_filial,c_is_new_account,c_is_balance,c_name,c_city,c_address,c_changed $basedir/static_csv/t_branch.csv && \
# ydb --ca-file $basedir/ca.crt import file csv --path main/t_ccy --columns c_ccy,c_description,c_number,c_group,c_currency_edit_field,c_minor_units,c_changed $basedir/static_csv/t_ccy.csv && \
# ydb --ca-file $basedir/ca.crt import file csv --path main/t_ccy_rate --columns c_ccy,c_date,c_rate,c_changed $basedir/static_csv/t_ccy_rate.csv && \
# ydb --ca-file $basedir/ca.crt import file csv --path main/t_filial --columns c_filial_id,c_bic,c_regno,c_description,c_date_current,c_changed $basedir/static_csv/t_filial.csv && \
# # Импортировать данные
# ydb --ca-file $basedir/ca.crt import file csv --path main/t_parameter --columns c_key,c_value,c_description,c_changed $basedir/static_csv/t_parameter.csv && \
# # Добавить асинхронный индекс
# ydb --ca-file $basedir/ca.crt yql -s 'ALTER TABLE `main/t_parameter` ADD INDEX idx_parameters_changed GLOBAL ASYNC ON (c_changed);'
