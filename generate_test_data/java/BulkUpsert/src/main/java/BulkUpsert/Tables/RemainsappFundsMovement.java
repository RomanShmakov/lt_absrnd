package BulkUpsert.Tables;

import com.google.gson.JsonObject;
import tech.ydb.proto.ValueProtos;
import tech.ydb.table.values.*;
import tech.ydb.table.values.proto.ProtoType;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class RemainsappFundsMovement {

    public static final StructType COLUMNS = StructType.of(
            Map.ofEntries(

                    Map.entry("c_document_id", PrimitiveType.Uuid),
                    Map.entry("c_funds_movement_number", PrimitiveType.Uint16),
                    Map.entry("c_funds_movement_status", PrimitiveType.Text),
                    Map.entry("c_funds_direction", PrimitiveType.Text),
                    Map.entry("c_account_id", PrimitiveType.Uuid),
                    Map.entry("c_amount_currency", DecimalType.of(25, 0)),
                    Map.entry("c_additional_info", PrimitiveType.Json),
                    Map.entry("c_hold_id", PrimitiveType.Uuid),
                    Map.entry("c_created", PrimitiveType.Timestamp),
                    Map.entry("c_changed", PrimitiveType.Timestamp)

            )
    );

    public static final List<String> PRIMARY_KEYS = Arrays.asList("c_document_id", "c_funds_movement_number");

    private final UUID c_document_id;
    private final Integer c_funds_movement_number;
    private final String c_funds_movement_status;
    private final String c_funds_direction;
    private final UUID c_account_id;
    private final DecimalValue c_amount_currency;
    private final PrimitiveValue c_additional_info;
    private final UUID c_hold_id;
    private final Instant c_created;
    private final Instant c_changed;

    public RemainsappFundsMovement(UUID c_document_id, Integer c_funds_movement_number, String c_funds_movement_status,
                                   String c_funds_direction, UUID c_account_id, DecimalValue c_amount_currency,
                                   PrimitiveValue c_additional_info, UUID c_hold_id, Instant c_created, Instant c_changed) {
        this.c_document_id = c_document_id;
        this.c_funds_movement_number = c_funds_movement_number;
        this.c_funds_movement_status = c_funds_movement_status;
        this.c_funds_direction = c_funds_direction;
        this.c_account_id = c_account_id;
        this.c_amount_currency = c_amount_currency;
        this.c_additional_info = c_additional_info;
        this.c_hold_id = c_hold_id;
        this.c_created = c_created;
        this.c_changed = c_changed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RemainsappFundsMovement other = (RemainsappFundsMovement) o;

        return Objects.equals(c_document_id, other.c_document_id)
                && Objects.equals(c_funds_movement_number, other.c_funds_movement_number)
                && Objects.equals(c_funds_movement_status, other.c_funds_movement_status)
                && Objects.equals(c_funds_direction, other.c_funds_direction)
                && Objects.equals(c_account_id, other.c_account_id)
                && Objects.equals(c_amount_currency, other.c_amount_currency)
                && Objects.equals(c_additional_info, other.c_additional_info)
                && Objects.equals(c_hold_id, other.c_hold_id)
                && Objects.equals(c_created, other.c_created)
                && Objects.equals(c_changed, other.c_changed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(c_document_id, c_funds_movement_number, c_funds_movement_status, c_funds_direction,
                c_account_id, c_amount_currency, c_additional_info, c_hold_id, c_created, c_changed);
    }

    @Override
    public String toString() {
        return "RemainsappFundsMovement{" +
                ", c_document_id=" + c_document_id +
                ", c_funds_movement_number='" + c_funds_movement_number +
                ", c_funds_movement_status='" + c_funds_movement_status +
                ", c_funds_direction=" + c_funds_direction +
                ", c_account_id=" + c_account_id +
                ", c_amount_currency=" + c_amount_currency +
                ", c_additional_info=" + c_additional_info +
                ", c_hold_id=" + c_hold_id +
                ", c_created=" + c_created +
                ", c_changed=" + c_changed +
                '}';
    }

    private Map<String, Value<?>> toValue() {
        return Map.ofEntries(
                Map.entry("c_document_id", PrimitiveValue.newUuid(c_document_id)),
                Map.entry("c_funds_movement_number", PrimitiveValue.newUint16(c_funds_movement_number)),
                Map.entry("c_funds_movement_status", PrimitiveValue.newText(c_funds_movement_status)),
                Map.entry("c_funds_direction", PrimitiveValue.newText(c_funds_direction)),
                Map.entry("c_account_id", PrimitiveValue.newUuid(c_account_id)),
                Map.entry("c_amount_currency", DecimalType.of(25,0).newValue(c_amount_currency.toString())),
                Map.entry("c_additional_info", c_additional_info == null ? VoidValue.of() : PrimitiveValue.newJson(c_additional_info.toString())),
                Map.entry("c_hold_id", c_hold_id == null ? VoidValue.of() : PrimitiveValue.newUuid(c_hold_id)),
                Map.entry("c_created", PrimitiveValue.newTimestamp(c_created)),
                Map.entry("c_changed", PrimitiveValue.newTimestamp(c_changed))
        );
    }

    public static ListValue toListValue(List<RemainsappFundsMovement> items) {
        ListType listType = ListType.of(COLUMNS);
        return listType.newValue(items.stream()
                .map(e -> COLUMNS.newValue(e.toValue()))
                .collect(Collectors.toList()));
    }
}
