package BulkUpsert.UpsertByTable;

import BulkUpsert.Tables.Balance;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.settings.BulkUpsertSettings;
import tech.ydb.table.values.DecimalType;
import tech.ydb.table.values.DecimalValue;
import tech.ydb.table.values.ListValue;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

public class BulkUpsertBalance {

    // Количество записей в пакете для отправки
    private final int batchSize = 10;
    // Общее количество записей, которые планируется записать в таблицу
    // (ДОЛЖНО БЫТЬ КРАТНО batchSize, ЧТОБЫ ЗАПИСАЛИСЬ ВСЕ ЗАПИСИ)

    private final int rowSize = 100;
    //

    // Путь к таблице Balance
    private static String tablePath;
    //

    // Данные для вставки в таблицу Balance
    private static final String ALLOWED_NUMBERS = "0123456789";
    private static final int AMOUNT_LENGTH = 20;
    private static final Random random = new SecureRandom();
    //

    public BulkUpsertBalance(String database, String tableName) {
        tablePath = database + "/" + tableName;
    }

    private void writeBatch(SessionRetryContext retryCtx, String tablePath, List<Balance> items) {
        ListValue rows = Balance.toListValue(items);
        retryCtx.supplyStatus(session -> session.executeBulkUpsert(
                tablePath, rows, new BulkUpsertSettings()
        )).join().expectSuccess("bulk upsert problem");
    }

    public void ImportData(SessionRetryContext retryCtx) {
        try {

            /*
                        Заполнение таблицы Balance
            */

            List<Balance> batchBalance = new ArrayList<>();
            int totalRecords = 0;
            for (int i = 0; i < rowSize; i++) {



                Balance newBalance = generateObjBalance();
                batchBalance.add(newBalance);
                // Выполняем пакетную запись в YDB при достижении batchSize
                if (batchBalance.size() == batchSize) {
                    writeBatch(retryCtx, tablePath, batchBalance);
                    batchBalance.clear();
                }
                totalRecords += 1;



            }

            System.out.println("Total records write in table: "  + tablePath +  " = " + totalRecords);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Balance generateObjBalance() {
        Instant now = Instant.now();
        UUID c_account_id = UUID.randomUUID();
        String c_balance_type = "CUSCOR";
        DecimalValue c_amount = DecimalType.of(25, 0).newValue(generateAmount());
        Instant c_changed = now;
        return new Balance(c_account_id, c_balance_type, c_amount, c_changed);
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
