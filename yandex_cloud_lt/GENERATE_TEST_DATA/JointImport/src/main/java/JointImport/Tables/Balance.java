package JointImport.Tables;

import tech.ydb.table.values.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class Balance {

    public static final StructType COLUMNS = StructType.of(
            Map.ofEntries(
                    Map.entry("c_account_id", PrimitiveType.Uuid),
                    Map.entry("c_balance_type", PrimitiveType.Text),
                    Map.entry("c_amount", DecimalType.of(20,0)),
                    Map.entry("c_changed", PrimitiveType.Timestamp)
            )
    );

    public static final List<String> PRIMARY_KEYS = Arrays.asList("c_account_id", "c_balance_type");

    private final UUID c_account_id;
    private final String c_balance_type;
    private final DecimalValue c_amount;
    private final Instant c_changed;

    public Balance(UUID c_account_id, String c_balance_type, DecimalValue c_amount, Instant c_changed) {
        this.c_account_id = c_account_id;
        this.c_balance_type = c_balance_type;
        this.c_amount = c_amount;
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

        Balance other = (Balance) o;

        return Objects.equals(c_account_id, other.c_account_id)
                && Objects.equals(c_balance_type, other.c_balance_type)
                && Objects.equals(c_amount, other.c_amount)
                && Objects.equals(c_changed, other.c_changed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(c_account_id, c_balance_type, c_amount, c_changed);
    }

    @Override
    public String toString() {
        return "Balance{" +
                ", c_account_id='" + c_account_id +
                ", c_balance_type=" + c_balance_type +
                ", c_amount=" + c_amount +
                ", c_changed=" + c_changed +
                '}';
    }

    private Map<String, Value<?>> toValue() {
        return Map.ofEntries(
                Map.entry("c_account_id", PrimitiveValue.newUuid(c_account_id)),
                Map.entry("c_balance_type", PrimitiveValue.newText(c_balance_type)),
                Map.entry("c_amount", DecimalType.of(20,0).newValue(c_amount.toString())),
                Map.entry("c_changed", PrimitiveValue.newTimestamp(c_changed))
        );
    }

    public static ListValue toListValue(List<Balance> items) {
        ListType listType = ListType.of(COLUMNS);
        return listType.newValue(items.stream()
                .map(e -> COLUMNS.newValue(e.toValue()))
                .collect(Collectors.toList()));
    }
}
