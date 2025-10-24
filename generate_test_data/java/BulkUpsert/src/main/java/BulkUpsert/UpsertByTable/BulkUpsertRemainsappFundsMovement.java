package BulkUpsert.UpsertByTable;

import BulkUpsert.Tables.RemainsappFundsMovement;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.settings.BulkUpsertSettings;
import tech.ydb.table.values.*;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BulkUpsertRemainsappFundsMovement {

    // Количество записей в пакете для отправки
    private final int batchSize = 10;
    // Общее количество записей, которые планируется записать в таблицу
    // (ДОЛЖНО БЫТЬ КРАТНО batchSize, ЧТОБЫ ЗАПИСАЛИСЬ ВСЕ ЗАПИСИ)
    private final int rowSize = 100;
    //

    // Путь к таблице RemainsappFundsMovement
    private static String tablePath;
    //

    // Данные для вставки в таблицу RemainsappFundsMovement
    private static final Random random = new SecureRandom();
    //

    public BulkUpsertRemainsappFundsMovement(String database, String tableName) {
        tablePath = database + "/" + tableName;
    }

    private void writeBatch(SessionRetryContext retryCtx, String tablePath, List<RemainsappFundsMovement> items) {
        ListValue rows = RemainsappFundsMovement.toListValue(items);
        retryCtx.supplyStatus(session -> session.executeBulkUpsert(
                tablePath, rows, new BulkUpsertSettings()
        )).join().expectSuccess("bulk upsert problem");
    }

    public void ImportData(SessionRetryContext retryCtx) {
        try {

            /*
                        Заполнение таблицы RemainsappFundsMovement
            */

            List<RemainsappFundsMovement> batchRemainsappFundsMovement = new ArrayList<>();
            int totalRecords = 0;
            for (int i = 0; i < rowSize; i++) {


                RemainsappFundsMovement newRemainsappFundsMovement = generateObjRemainsappFundsMovement();
                batchRemainsappFundsMovement.add(newRemainsappFundsMovement);
                // Выполняем пакетную запись в YDB при достижении batchSize
                if (batchRemainsappFundsMovement.size() == batchSize) {
                    writeBatch(retryCtx, tablePath, batchRemainsappFundsMovement);
                    batchRemainsappFundsMovement.clear();
                }
                totalRecords += 1;


            }

            System.out.println("Total records write in table: "  + tablePath +  " = " + totalRecords);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RemainsappFundsMovement generateObjRemainsappFundsMovement() {
        Instant now = Instant.now();

        UUID c_document_id = UUID.randomUUID();
        Integer c_funds_movement_number = random.nextInt(2) + 1;
        String c_funds_movement_status = (random.nextBoolean() ? "DONE" : "CANCELED");
        String c_funds_direction = (random.nextBoolean() ? "FUNDS_DIRECTION_DEBIT" : "FUNDS_DIRECTION_CREDIT");
        UUID c_account_id = UUID.randomUUID();
        DecimalValue c_amount_currency = DecimalType.of(25, 0).newValue(random.nextInt(1000000) + 1);
        PrimitiveValue c_additional_info = null;
        UUID c_hold_id = UUID.randomUUID();
        Instant c_created = now;
        Instant c_changed = now;

        return new RemainsappFundsMovement(c_document_id, c_funds_movement_number, c_funds_movement_status,
                c_funds_direction, c_account_id, c_amount_currency, c_additional_info, c_hold_id, c_created, c_changed);
    }

}
