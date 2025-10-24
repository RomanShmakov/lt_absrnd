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


public final class InsertDateInCcyAndFilial {
    // задаем информацию для подключения к БД
    private static final String usernameYDB = "user1";
    private static final String passwordYDB = "passw0rd";
    private static final String connectionString = "grpcs://rnd-ydb1.moscow.alfaintra.net:2136/?database=/Root/asdb";
    private static final String saKeyFile = "C:\\ydb\\ca.crt";
    private static final Logger logger = LoggerFactory.getLogger(InsertDateInCcyAndFilial.class);
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
                                    Заполнение 2х таблиц: t_account, t_balance, t_customer
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
        StringBuilder sb_t_ccy_rate = new StringBuilder();
        StringBuilder sb_t_filial = new StringBuilder();
        Map<String, Value<?>> paramMap_t_ccy_rate = new HashMap<>();
        Map<String, Value<?>> paramMap_t_filial = new HashMap<>();
        List<Balance> batchBalance = new ArrayList<>();
        List<Customer> batchCustomer = new ArrayList<>();

        // Генерируем параметры для запроса
        // t_ccy_rate
        sb_t_ccy_rate.append("( $c_ccy")
                .append(", $c_date")
                .append(", $c_rate")
                .append(", $c_changed)");

        // t_filial
        sb_t_filial.append("($c_filial")
                .append(", $c_bic")
                .append(", $c_regno")
                .append(", $c_description")
                .append(", $c_EOD_switch_time")
                .append(", $c_date_current")
                .append(", $c_changed)");

        // Генерируем значения для вставки в запрос
        // t_ccy_rate
        Instant now = Instant.now();
        String c_ccy = "RUR";
        Instant c_date = now;
        DecimalValue c_rate = DecimalType.of(15, 9).newValue(generateAmount());
        Instant c_changed = now;
        // t_filial
        String c_filial = "filial1";
        String c_bic = "044525593";
        String c_regno = "1326";
        String c_description = "АО \"АЛЬФА-БАНК\"";
        Instant c_EOD_switch_time = Instant.EPOCH;
        Instant c_date_current = now;

        // Вставляем значения
        // t_ccy_rate
        paramMap_t_ccy_rate.put("c_ccy", PrimitiveValue.newText(c_ccy));
        paramMap_t_ccy_rate.put("c_date", PrimitiveValue.newDate(c_date));
        paramMap_t_ccy_rate.put("c_rate", DecimalType.of(15, 9).newValue(c_rate.toString()));
        paramMap_t_ccy_rate.put("c_changed", PrimitiveValue.newDate(c_changed));

        // t_filial
        paramMap_t_filial.put("c_filial", PrimitiveValue.newText(c_filial));
        paramMap_t_filial.put("c_bic", PrimitiveValue.newText(c_bic));
        paramMap_t_filial.put("c_regno", PrimitiveValue.newText(c_regno));
        paramMap_t_filial.put("c_description", PrimitiveValue.newText(c_description));
        paramMap_t_filial.put("c_EOD_switch_time", PrimitiveValue.newDate(c_EOD_switch_time));
        paramMap_t_filial.put("c_date_current", PrimitiveValue.newDate(c_date_current));
        paramMap_t_filial.put("c_changed", PrimitiveValue.newDate(c_changed));

        // Параметры для запроса t_ccy_rate
        String valuesForQuery_t_ccy_rate = sb_t_ccy_rate.toString();
        String valuesForQuery_t_filial = sb_t_filial.toString();
        // Значения для параметров запроса

        Params params_t_ccy_rate = Params.copyOf(paramMap_t_ccy_rate);
        Params params_t_filial = Params.copyOf(paramMap_t_filial);
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
                + valuesForQuery_t_ccy_rate
                + ";";

        // Отправка запроса
        //account
        retryCtx.supplyStatus(session -> {
            TableTransaction transaction = session.createNewTransaction(TxMode.SERIALIZABLE_RW);
            // DataQueryResult result = transaction.executeDataQuery(query, params).join().getValue();
            transaction.executeDataQuery(query, params_t_ccy_rate).join().getValue();
            return transaction.commit();
        }).join().expectSuccess("insert in t_account problem");


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

}