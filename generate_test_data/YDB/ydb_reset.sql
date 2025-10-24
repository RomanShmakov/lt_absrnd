-- Удаление таблиц

DROP TABLE IF EXISTS `main/t_parameter`;
DROP TABLE IF EXISTS `main/t_ccy`;
DROP TABLE IF EXISTS `main/t_ccy_rate`;
-- DROP TABLE IF EXISTS `main/t_account`;
--DROP TABLE IF EXISTS `main/t_document`;
DROP TABLE IF EXISTS `main/t_posting`;
--DROP TABLE IF EXISTS `main/t_hstbal`;
DROP TABLE IF EXISTS `main/t_filial`;
DROP TABLE IF EXISTS `main/t_branch`;
-- DROP TABLE IF EXISTS `remains/t_balance`;

-- Создание таблиц

-- Параметры
CREATE TABLE `main/t_parameter` (
  c_key utf8 NOT NULL,
  c_value utf8,
  c_description utf8 NOT NULL,
  c_changed Timestamp NOT NULL,
  PRIMARY KEY (c_key),
) WITH (
  AUTO_PARTITIONING_BY_SIZE = ENABLED,
  AUTO_PARTITIONING_BY_LOAD = ENABLED,
  AUTO_PARTITIONING_MIN_PARTITIONS_COUNT = 40,
  AUTO_PARTITIONING_MAX_PARTITIONS_COUNT = 100,
  AUTO_PARTITIONING_PARTITION_SIZE_MB = 1024
);

-- Описание валют
CREATE TABLE `main/t_ccy` (
  c_ccy utf8 NOT NULL,
  c_description utf8 NOT NULL,
  c_number utf8 NOT NULL,
  c_group utf8 NOT NULL,
  c_currency_edit_field Uint16 NOT NULL,
  c_minor_units Uint16 NOT NULL,
  c_changed Timestamp NOT NULL,
  PRIMARY KEY (c_ccy)
) WITH (
  AUTO_PARTITIONING_BY_SIZE = ENABLED,
  AUTO_PARTITIONING_PARTITION_SIZE_MB = 1024,
  AUTO_PARTITIONING_BY_LOAD = ENABLED,
  AUTO_PARTITIONING_MIN_PARTITIONS_COUNT = 40,
  AUTO_PARTITIONING_MAX_PARTITIONS_COUNT = 100
);

-- -- Счета
-- CREATE TABLE `main/t_account` (
--   -- Идентификатор счета
--   c_account_id uuid NOT NULL,
--   -- Идентификтор клиента (t_customer)
--   c_customer_id utf8,
--   -- 20-значный номер счета
--   c_account_number utf8 NOT NULL,
--   -- Валюта
--   c_ccy utf8 NOT NULL,
--   -- Филиал
--   c_filial_id utf8 NOT NULL,
--   -- Дата открытия
--   c_open_date date NOT NULL,
--   -- Дата закрытия
--   c_close_date date,
--   c_is_active bool NOT NULL,
--   -- Признак недоступности счет для списания средств
--   c_is_blocked_debit bool NOT NULL,
--   -- Признак недоступности счет для зачисления
--   c_is_blocked_credit bool NOT NULL,
--   -- Признак блокировки счета
--   c_is_blocked bool NOT NULL,
--   -- Признак необходимости проверки остатка
--   c_balance_check utf8 NOT NULL,
--   c_changed timestamp NOT NULL,
--   PRIMARY KEY (c_account_id),
-- ) WITH (
--   AUTO_PARTITIONING_BY_SIZE = ENABLED,
--   AUTO_PARTITIONING_BY_LOAD = ENABLED,
--   AUTO_PARTITIONING_MIN_PARTITIONS_COUNT = 40,
--   AUTO_PARTITIONING_MAX_PARTITIONS_COUNT = 100,
--   AUTO_PARTITIONING_PARTITION_SIZE_MB = 1024
-- );

-- -- Документы
-- CREATE TABLE `main/t_document` (
--   -- id документа
--   c_document_id uuid NOT NULL,
--   -- Дата документа
--   c_date date NOT NULL,
--   -- Тип документа
--   c_type utf8 NOT NULL,
--   -- Номер документа
--   c_number utf8 NOT NULL,
--   -- Референс
--   c_reference utf8,
--   -- 20-значный номер cчёта дебета
--   c_debit_account_number utf8,
--   -- 20-значный номер cчёта кредита  
--   c_credit_account_number utf8,
--   -- Сумма  
--   c_amount decimal(25, 0) not null,
--   -- Назначение платежа
--   c_narrative utf8,
--   -- Время изменения
--   c_changed timestamp NOT NULL,
--   PRIMARY KEY (c_document_id)
-- ) WITH (
--   AUTO_PARTITIONING_BY_SIZE = ENABLED,
--   AUTO_PARTITIONING_BY_LOAD = ENABLED,
--   AUTO_PARTITIONING_MIN_PARTITIONS_COUNT = 40,
--   AUTO_PARTITIONING_MAX_PARTITIONS_COUNT = 100,
--   AUTO_PARTITIONING_PARTITION_SIZE_MB = 1024
-- );

-- Полупроводки
CREATE TABLE `main/t_posting` (
  -- Идентификатор связанного документа
  c_document_id uuid NOT NULL,
  -- Счетчик проводок в рамках одного c_document_id
  c_posting_number Uint8 NOT NULL,
  -- Дата ввода проводки
  c_date date NOT NULL,
  -- Дата учёта в балансе
  c_posting_balance_date date NOT NULL,
  -- Идентификатор счета
  c_account_id uuid NOT NULL,
  -- Направление движения средств
  c_funds_direction utf8 NOT NULL,
  -- Сумма в валюте счёта
  c_amount decimal(25, 0) NOT NULL,
  -- Рублёвый эквивалент
  c_amount_base decimal(25, 0) NOT NULL,
  -- Системная дата/временя ввода проводки
  c_created timestamp NOT NULL,
  -- Необязательные атрибуты проводки
  c_attributes Json,
  PRIMARY KEY (c_document_id, c_posting_number)
) WITH (
  AUTO_PARTITIONING_BY_SIZE = ENABLED,
  AUTO_PARTITIONING_BY_LOAD = ENABLED,
  AUTO_PARTITIONING_MIN_PARTITIONS_COUNT = 40,
  AUTO_PARTITIONING_MAX_PARTITIONS_COUNT = 100,
  AUTO_PARTITIONING_PARTITION_SIZE_MB = 1024
);

-- -- История остатков
-- CREATE TABLE `main/t_hstbal` (
--   -- Идентификатор счета (t_account)
--   c_account_id uuid NOT NULL,
--   -- Дата учета движения/остатка в балансе
--   c_baldate date NOT NULL,
--   -- Входящий остаток в валюте
--   c_inp_ccy decimal(25, 0) NOT NULL,
--   -- Оборот в валюте Дт 
--   c_dr_ccy decimal(25, 0) NOT NULL,
--   -- Оборот в валюте Кт
--   c_cr_ccy decimal(25, 0) NOT NULL,
--   -- Исходящий остаток в валюте 
--   c_out_ccy decimal(25, 0) NOT NULL,
--   -- Входящий остаток в RUR 
--   c_inp_base decimal(25, 0) NOT NULL,
--   -- Оборот в RUR Дт
--   c_dr_base decimal(25, 0) NOT NULL,
--   -- Оборот в RUR Кт
--   c_cr_base decimal(25, 0) NOT NULL,
--   -- Исходящий остаток в RUR
--   c_out_base decimal(25, 0) NOT NULL,
--   -- Дата/время изменения 
--   c_changed timestamp NOT NULL,
--   PRIMARY KEY (c_account_id, c_baldate)
-- ) WITH (
--   AUTO_PARTITIONING_BY_SIZE = ENABLED,
--   AUTO_PARTITIONING_BY_LOAD = ENABLED,
--   AUTO_PARTITIONING_MIN_PARTITIONS_COUNT = 40,
--   AUTO_PARTITIONING_MAX_PARTITIONS_COUNT = 100,
--   AUTO_PARTITIONING_PARTITION_SIZE_MB = 1024
-- );

-- Курсы валют
CREATE TABLE `main/t_ccy_rate` (
  c_ccy utf8 NOT NULL,
  c_date date NOT NULL,
  c_rate decimal(25, 10) NOT NULL,
  c_changed timestamp NOT NULL,
  PRIMARY KEY (c_ccy, c_date)
) WITH (
  AUTO_PARTITIONING_BY_SIZE = ENABLED,
  AUTO_PARTITIONING_BY_LOAD = ENABLED,
  AUTO_PARTITIONING_MIN_PARTITIONS_COUNT = 40,
  AUTO_PARTITIONING_MAX_PARTITIONS_COUNT = 100,
  AUTO_PARTITIONING_PARTITION_SIZE_MB = 1024
);

-- Филиалы
CREATE TABLE `main/t_filial` (
  c_filial_id utf8 NOT NULL,
  c_bic utf8 NOT NULL,
  c_regno utf8 NOT NULL,
  c_description utf8 NOT NULL,
  -- время переключения бизнес-даты
  c_EOD_switch_time datetime,
  -- текущая бизнес-дата филиала
  c_date_current date,
  -- дата фиксации баланса филиала
  c_date_closed date,
  c_changed timestamp NOT NULL,
  PRIMARY KEY (c_filial_id)
) WITH (
  AUTO_PARTITIONING_BY_SIZE = ENABLED,
  AUTO_PARTITIONING_BY_LOAD = ENABLED,
  AUTO_PARTITIONING_MIN_PARTITIONS_COUNT = 40,
  AUTO_PARTITIONING_MAX_PARTITIONS_COUNT = 100,
  AUTO_PARTITIONING_PARTITION_SIZE_MB = 1024
);

-- Отделения
CREATE TABLE `main/t_branch` (
  c_branch_id utf8 NOT NULL,
  c_filial utf8 NOT NULL,
  -- Можно ли открывать новые счета
  c_is_new_account bool not NULL,
  c_is_balance bool NOT NULL,
  c_name utf8 NOT NULL,
  c_city utf8 NOT NULL,
  c_address utf8 NOT NULL,
  c_changed timestamp NOT NULL,
  PRIMARY KEY (c_branch_id)
) WITH (
  AUTO_PARTITIONING_BY_SIZE = ENABLED,
  AUTO_PARTITIONING_BY_LOAD = ENABLED,
  AUTO_PARTITIONING_MIN_PARTITIONS_COUNT = 40,
  AUTO_PARTITIONING_MAX_PARTITIONS_COUNT = 100,
  AUTO_PARTITIONING_PARTITION_SIZE_MB = 1024
);

-- -- Остатки
-- CREATE TABLE `remains/t_balance` (
--  -- Номер счета (t_account)
--  c_account_id uuid NOT NULL,
--  -- Тип баланса (t_balance_type)
--  c_balance_type Utf8 NOT NULL,
--  -- Сумма
--  c_amount decimal(25, 0) NOT NULL,
--  -- Время изменения
--  c_changed timestamp NOT NULL,
--  PRIMARY KEY (c_account_id, c_balance_type)
-- ) WITH (
--  AUTO_PARTITIONING_BY_SIZE = ENABLED,
--  AUTO_PARTITIONING_BY_LOAD = ENABLED,
--  AUTO_PARTITIONING_MIN_PARTITIONS_COUNT = 40,
--  AUTO_PARTITIONING_MAX_PARTITIONS_COUNT = 100,
--  AUTO_PARTITIONING_PARTITION_SIZE_MB = 1024
-- );
