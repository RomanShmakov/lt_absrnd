package SimpleInsert;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.ydb.common.transaction.TxMode;
import tech.ydb.core.auth.StaticCredentials;
import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.TableClient;
import tech.ydb.table.query.DataQueryResult;
import tech.ydb.table.query.Params;
import tech.ydb.table.values.Value;
import tech.ydb.table.transaction.TableTransaction;
import tech.ydb.table.values.PrimitiveValue;


public final class SimpleInsert {

    // задаем информацию для подключения к БД
    private static final String usernameYDB = "user1";
    private static final String passwordYDB = "passw0rd";
    private static final String connectionString = "grpcs://rnd-ydb1.moscow.alfaintra.net:2136/?database=/Root/asdb";
    private static final String saKeyFile = "C:\\ydb\\ca.crt";
    private static final Logger logger = LoggerFactory.getLogger(SimpleInsert.class);
    //

    // Количество запросов
    private static final int COUNT_QUERY = 1;
    // Количество строк Values в одном запросе
    private static final int COUNT_ROW_IN_QUERY = 100;
    // ИТОГ ЗАПИСАННЫХ СТРОК = COUNT_QUERY * COUNT_ROW_IN_QUERY
    //

    // данные для вставки в таблицу
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String ALLOWED_NUMBERS = "0123456789";
    private static final int CUSTOMER_ID_LENGTH = 20;
    private static final int ACCOUNT_NUMBER_LENGTH = 20;
    private static final Random random = new SecureRandom();
    //

    public static void main(String[] args) {
        try {
            byte[] certBytes = Files.readAllBytes(Paths.get(saKeyFile));
            StaticCredentials authProvider = new StaticCredentials(usernameYDB, passwordYDB);
            try (GrpcTransport transport = GrpcTransport.forConnectionString(connectionString)
                    .withSecureConnection(certBytes)
                    .withAuthProvider(authProvider)
                    .build()) {
                try (TableClient tableClient = TableClient.newClient(transport).build()) {
                    SessionRetryContext retryCtx = SessionRetryContext.create(tableClient).build();

                    String tableName = "tpcc/t_account";

                    int totalRecords = 0;
                    //TODO: изменить количество выполнений запроса Insert
                    for (int i = 0; i < COUNT_QUERY; i++) {
                        try {


                            
                            insertQuery(retryCtx, tableName);
                            totalRecords += 1;


                        } catch (RuntimeException e) {
                            logger.info("Ошибка вставки. Дублирующий номер счета");
                        }
                    }
                    System.out.println("Total records write in table: "  + tableName +  " = " + totalRecords);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void insertQuery(SessionRetryContext retryCtx, String tableName) {
        // Генерация нескольких строк Values, чтобы подставить в них значения
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= COUNT_ROW_IN_QUERY; i++) {
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
            if (i < COUNT_ROW_IN_QUERY) sb.append(","); // запятая и перенос строки между записями
        }
        String valuesForQuery = sb.toString();

        // Генерируем значения для вставки в запрос
        Instant now = Instant.now();
        Map<String, Value<?>> paramMap = new HashMap<>();
        for (int i = 1; i <= COUNT_ROW_IN_QUERY; i++) {
            paramMap.put("$c_account_id_" + i, PrimitiveValue.newUuid(UUID.randomUUID()));
            paramMap.put("$c_customer_id_" + i, PrimitiveValue.newText(generateCustomerId()));
            paramMap.put("$c_account_number_" + i, PrimitiveValue.newText(generateAccountNumber()));
            paramMap.put("$c_ccy_" + i, PrimitiveValue.newText("RUR"));
            paramMap.put("$c_filial_id_" + i, PrimitiveValue.newText("filial1"));
            paramMap.put("$c_open_date_" + i, PrimitiveValue.newDate(now));
            paramMap.put("$c_is_active_" + i, PrimitiveValue.newBool(true));
            paramMap.put("$c_is_blocked_debit_" + i, PrimitiveValue.newBool(false));
            paramMap.put("$c_is_blocked_credit_" + i, PrimitiveValue.newBool(false));
            paramMap.put("$c_is_blocked_" + i, PrimitiveValue.newBool(false));
            paramMap.put("$c_balance_check_" + i, PrimitiveValue.newText(""));
            paramMap.put("$c_changed_" + i, PrimitiveValue.newTimestamp(now));
        }

        retryCtx.supplyStatus(session -> {
            // Создаем новую транзакцию
            TableTransaction transaction = session.createNewTransaction(TxMode.SERIALIZABLE_RW);
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
                    + "INSERT INTO `" + tableName + "` ("
                    + "  c_account_id, c_customer_id, c_account_number, "
                    + "  c_ccy, c_filial_id, c_open_date, c_close_date, "
                    + "  c_is_active, c_is_blocked_debit, c_is_blocked_credit, "
                    + "  c_is_blocked, c_balance_check, c_changed)"
                    + " VALUES "
                    + valuesForQuery
                    + ";";

            Params params = Params.copyOf(paramMap);

            // Выполнение запроса
            // DataQueryResult result = transaction.executeDataQuery(query, params).join().getValue();
            transaction.executeDataQuery(query, params).join().getValue();

            // Коммит транзакции
            return transaction.commit();
        }).join().expectSuccess("Ошибка при вставке счета");
    }

    public static String generateValuesRequest() {
        StringBuilder sb = new StringBuilder(CUSTOMER_ID_LENGTH);
        for (int i = 0; i < CUSTOMER_ID_LENGTH; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARS.length());
            char randomChar = ALLOWED_CHARS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
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

}