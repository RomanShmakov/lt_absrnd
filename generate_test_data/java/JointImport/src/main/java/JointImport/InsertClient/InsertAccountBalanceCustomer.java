package JointImport.InsertClient;

import JointImport.Tables.Balance;
import JointImport.Tables.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.ydb.common.transaction.TxMode;
import tech.ydb.core.auth.StaticCredentials;
import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.TableClient;
import tech.ydb.table.query.Params;
import tech.ydb.table.settings.BulkUpsertSettings;
import tech.ydb.table.transaction.TableTransaction;
import tech.ydb.table.values.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;


public final class InsertAccountBalanceCustomer {
    // задаем информацию для подключения к БД
    private static final String usernameYDB = "user1";
    private static final String passwordYDB = "passw0rd";
    private static final String connectionString = "grpcs://rnd-ydb1.moscow.alfaintra.net:2136/?database=/Root/asdb";
    private static final String saKeyFile = "C:\\ydb\\ca.crt";
    private static final Logger logger = LoggerFactory.getLogger(InsertAccountBalanceCustomer.class);
    // Имя таблицы
    private static final String tableNameAccount = "tpcc/t_account";
    private static final String tableNameBalance = "/Root/asdb/tpcc/t_balance";
    private static final String tableNameCustomer = "/Root/asdb/tpcc/t_customer";
    //
    // Количество запросов
    private static final int COUNT_QUERY = 1;
    // Количество (строк Values / объектов BulkUpsert) в одном запросе
    private static final int COUNT_ROW_IN_QUERY = 100;
    // ИТОГ ЗАПИСАННЫХ СТРОК = COUNT_QUERY * COUNT_ROW_IN_QUERY
    //
    // Данные для вставки в таблицу
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String ALLOWED_LETETRS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALLOWED_NUMBERS = "0123456789";
    private static final int RANDOM_STRING_LENGTH = 10;
    private static final int CUSTOMER_ID_LENGTH = 20;
    private static final int ACCOUNT_NUMBER_LENGTH = 20;
    private static final int AMOUNT_LENGTH = 20;
    private static final Random random = new SecureRandom();

    //
    public static void InsertByOneAccount() {
        try {
            byte[] certBytes = Files.readAllBytes(Paths.get(saKeyFile));
            StaticCredentials authProvider = new StaticCredentials(usernameYDB, passwordYDB);
            try (GrpcTransport transport = GrpcTransport.forConnectionString(connectionString)
                    .withSecureConnection(certBytes)
                    .withAuthProvider(authProvider)
                    .build()) {
                try (TableClient tableClient = TableClient.newClient(transport).build()) {
                    SessionRetryContext retryCtx = SessionRetryContext.create(tableClient).build();


                    //TODO: изменить количество выполнений запроса Insert
                    for (int i = 0; i < COUNT_QUERY; i++) {
                        try {


                            /*
                                    Заполнение 3х таблиц: t_account, t_balance, t_customer
                            */

                            insertQuery(retryCtx);

                        } catch (RuntimeException e) {
                            logger.info("Ошибка вставки. Дублирующий номер счета");
                        }
                    }
                    System.out.println("Total records write in tables: "
                            + tableNameAccount + ", "
                            + tableNameBalance + ", "
                            + tableNameCustomer + " = "
                            + COUNT_QUERY * COUNT_ROW_IN_QUERY);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    public static void insertQuery(SessionRetryContext retryCtx) {
        // Переменные для генерации запроса
        StringBuilder sb = new StringBuilder();
        Map<String, Value<?>> paramMap = new HashMap<>();
        List<Balance> batchBalance = new ArrayList<>();
        List<Customer> batchCustomer = new ArrayList<>();
        // Генерируем параметры для запроса
        for (int i = 1; i <= COUNT_ROW_IN_QUERY; i++) {

            // Генерация нескольких строк Values, чтобы подставить в них значения
            // account
            sb.append("($c_account_id_").append(i)
                    .append(", $c_customer_id_").append(i)
                    .append(", $c_account_number_").append(i)
                    .append(", $c_ccy_").append(i)
                    .append(", $c_filial_id_").append(i)
                    .append(", $c_open_date_").append(i)
                    .append(", NULL")
                    .append(", $c_is_active_").append(i)
                    .append(", $c_is_blocked_debit_").append(i)
                    .append(", $c_is_blocked_credit_").append(i)
                    .append(", $c_is_blocked_").append(i)
                    .append(", $c_balance_check_").append(i)
                    .append(", $c_changed_").append(i)
                    .append(")");
            // запятая между записями
            if (i < COUNT_ROW_IN_QUERY) sb.append(",");
            //

            // Генерируем значения для вставки в запрос
            // account
            Instant now = Instant.now();
            UUID c_account_id = UUID.randomUUID();
            String c_customer_id = generateCustomerId();
            String c_account_number = generateAccountNumber();
            String c_ccy = "RUR";
            String c_filial_id = "filial1";
            Instant c_open_date = now;
            boolean c_is_active = true;
            boolean c_is_blocked_debit = false;
            boolean c_is_blocked_credit = false;
            boolean c_is_blocked = false;
            String c_balance_check = "";
            Instant c_changed = now;
            // balance
            String c_balance_type = "CUSCOR";
            DecimalValue c_amount = DecimalType.of(25, 0).newValue(generateAmount());
            // customer
            String c_type = generateString();
            String c_category = generateString();
            String c_name = generateString();
            Instant c_close_date = null;
            String c_resdnt = generateString();
            String c_inn = generateString();
            //

            // Вставляем значения
            // account
            paramMap.put("$c_account_id_" + i, PrimitiveValue.newUuid(c_account_id));
            paramMap.put("$c_customer_id_" + i, PrimitiveValue.newText(c_customer_id));
            paramMap.put("$c_account_number_" + i, PrimitiveValue.newText(c_account_number));
            paramMap.put("$c_ccy_" + i, PrimitiveValue.newText(c_ccy));
            paramMap.put("$c_filial_id_" + i, PrimitiveValue.newText(c_filial_id));
            paramMap.put("$c_open_date_" + i, PrimitiveValue.newDate(c_open_date));
            paramMap.put("$c_is_active_" + i, PrimitiveValue.newBool(c_is_active));
            paramMap.put("$c_is_blocked_debit_" + i, PrimitiveValue.newBool(c_is_blocked_debit));
            paramMap.put("$c_is_blocked_credit_" + i, PrimitiveValue.newBool(c_is_blocked_credit));
            paramMap.put("$c_is_blocked_" + i, PrimitiveValue.newBool(c_is_blocked));
            paramMap.put("$c_balance_check_" + i, PrimitiveValue.newText(c_balance_check));
            paramMap.put("$c_changed_" + i, PrimitiveValue.newTimestamp(c_changed));
            // balance
            batchBalance.add(new Balance(c_account_id, c_balance_type, c_amount, c_changed));
            batchCustomer.add(new Customer(c_customer_id, c_type, c_category, c_name, c_open_date, c_close_date, c_resdnt, c_inn, c_changed));
        }
        // Параметры для запроса
        String valuesForQuery = sb.toString();
        // Значения для параметров запроса
        Params params = Params.copyOf(paramMap);
        // SQL-запрос с параметрами
        String query = "DECLARE $c_account_id AS Uuid; "
                + "DECLARE $c_customer_id AS Utf8; "
                + "DECLARE $c_account_number AS Utf8; "
                + "DECLARE $c_ccy AS Utf8; "
                + "DECLARE $c_filial_id AS Utf8; "
                + "DECLARE $c_open_date AS Date; "
                + "DECLARE $c_is_active AS Bool; "
                + "DECLARE $c_is_blocked_debit AS Bool; "
                + "DECLARE $c_is_blocked_credit AS Bool; "
                + "DECLARE $c_is_blocked AS Bool; "
                + "DECLARE $c_balance_check AS Utf8; "
                + "DECLARE $c_changed AS Timestamp; "
                + "INSERT INTO `" + tableNameAccount + "` ("
                + "  c_account_id, c_customer_id, c_account_number, "
                + "  c_ccy, c_filial_id, c_open_date, c_close_date, "
                + "  c_is_active, c_is_blocked_debit, c_is_blocked_credit, "
                + "  c_is_blocked, c_balance_check, c_changed)"
                + " VALUES "
                + valuesForQuery
                + ";";

        // Отправка запроса
        //account
        retryCtx.supplyStatus(session -> {
            TableTransaction transaction = session.createNewTransaction(TxMode.SERIALIZABLE_RW);
            // DataQueryResult result = transaction.executeDataQuery(query, params).join().getValue();
            transaction.executeDataQuery(query, params).join().getValue();
            return transaction.commit();
        }).join().expectSuccess("insert in t_account problem");
        // balance
        ListValue rowsBalance = Balance.toListValue(batchBalance);
        retryCtx.supplyStatus(session -> session.executeBulkUpsert(
                tableNameBalance, rowsBalance, new BulkUpsertSettings()
        )).join().expectSuccess("bulk upsert in t_balance problem");
        // customer
        ListValue rowsCustomer = Customer.toListValue(batchCustomer);
        retryCtx.supplyStatus(session -> session.executeBulkUpsert(
                tableNameCustomer, rowsCustomer, new BulkUpsertSettings()
        )).join().expectSuccess("bulk upsert in t_customer problem");
        //

    }

    /**
     * Генерирует случайный 20-символьный идентификатор клиента
     * из символов A-Z и цифр 0-9.
     *
     * @return Строка из 20 символов в верхнем регистре
     */
    public static String generateCustomerId() {
        StringBuilder sb = new StringBuilder(CUSTOMER_ID_LENGTH);
        for (int i = 0; i < CUSTOMER_ID_LENGTH; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARS.length());
            char randomChar = ALLOWED_CHARS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    /**
     * Генерирует случайный 20-символьный номер счета
     * из цифр 0-9.
     *
     * @return Строка из 20 символов
     */
    public static String generateAccountNumber() {
        StringBuilder sb = new StringBuilder(ACCOUNT_NUMBER_LENGTH);
        for (int i = 0; i < ACCOUNT_NUMBER_LENGTH; i++) {
            int randomIndex = random.nextInt(ALLOWED_NUMBERS.length());
            char randomChar = ALLOWED_NUMBERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    /**
     * Генерирует случайную 20-символьную сумму счета
     * из цифр 0-9.
     *
     * @return Строка из 20 символов
     */
    public static String generateAmount() {
        StringBuilder sb = new StringBuilder(AMOUNT_LENGTH);
        for (int i = 0; i < AMOUNT_LENGTH; i++) {
            int randomIndex = random.nextInt(ALLOWED_NUMBERS.length());
            char randomChar = ALLOWED_NUMBERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    /**
     * Генерирует случайную 20-символьную сумму счета
     * из цифр 0-9.
     *
     * @return Строка из 20 символов
     */
    public static String generateString() {
        StringBuilder sb = new StringBuilder(RANDOM_STRING_LENGTH);
        for (int i = 0; i < RANDOM_STRING_LENGTH; i++) {
            int randomIndex = random.nextInt(ALLOWED_LETETRS.length());
            char randomChar = ALLOWED_LETETRS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

}