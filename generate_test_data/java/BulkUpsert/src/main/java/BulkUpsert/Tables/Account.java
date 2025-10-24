package BulkUpsert.Tables;

import tech.ydb.table.values.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class Account {

    public static final StructType COLUMNS = StructType.of(
        Map.ofEntries(
            Map.entry("c_account_id", PrimitiveType.Uuid),
            Map.entry("c_customer_id", PrimitiveType.Text),
            Map.entry("c_account_number", PrimitiveType.Text),
            Map.entry("c_ccy", PrimitiveType.Text),
            Map.entry("c_filial_id", PrimitiveType.Text),
            Map.entry("c_open_date", PrimitiveType.Date),
            Map.entry("c_close_date", PrimitiveType.Date),
            Map.entry("c_is_active", PrimitiveType.Bool),
            Map.entry("c_is_blocked_debit", PrimitiveType.Bool),
            Map.entry("c_is_blocked_credit", PrimitiveType.Bool),
            Map.entry("c_is_blocked", PrimitiveType.Bool),
            Map.entry("c_balance_check", PrimitiveType.Text),
            Map.entry("c_changed", PrimitiveType.Timestamp)
        )
    );

    public static final List<String> PRIMARY_KEYS = Arrays.asList("c_account_id");

    private final UUID c_account_id;
    private final String c_customer_id;
    private final String c_account_number;
    private final String c_ccy;
    private final String c_filial_id;
    private final Instant c_open_date;
    private final Instant c_close_date;
    private final Boolean c_is_active;
    private final Boolean c_is_blocked_debit;
    private final Boolean c_is_blocked_credit;
    private final Boolean c_is_blocked;
    private final String c_balance_check;
    private final Instant c_changed;

    public Account(UUID c_account_id, String c_customer_id, String c_account_number, String c_ccy, String c_filial_id,
                   Instant c_open_date, Instant c_close_date, Boolean c_is_active, Boolean c_is_blocked_debit,
                   Boolean c_is_blocked_credit, Boolean c_is_blocked, String c_balance_check, Instant c_changed) {
        this.c_account_id = c_account_id;
        this.c_customer_id = c_customer_id;
        this.c_account_number = c_account_number;
        this.c_ccy = c_ccy;
        this.c_filial_id = c_filial_id;
        this.c_open_date = c_open_date;
        this.c_close_date = c_close_date;
        this.c_is_active = c_is_active;
        this.c_is_blocked_debit = c_is_blocked_debit;
        this.c_is_blocked_credit = c_is_blocked_credit;
        this.c_is_blocked = c_is_blocked;
        this.c_balance_check = c_balance_check;
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

        Account other = (Account) o;

        return Objects.equals(c_customer_id, other.c_customer_id)
                && Objects.equals(c_account_id, other.c_account_id)
                && Objects.equals(c_account_number, other.c_account_number)
                && Objects.equals(c_ccy, other.c_ccy)
                && Objects.equals(c_filial_id, other.c_filial_id)
                && Objects.equals(c_open_date, other.c_open_date)
                && Objects.equals(c_close_date, other.c_close_date)
                && Objects.equals(c_is_active, other.c_is_active)
                && Objects.equals(c_is_blocked_debit, other.c_is_blocked_debit)
                && Objects.equals(c_is_blocked_credit, other.c_is_blocked_credit)
                && Objects.equals(c_is_blocked, other.c_is_blocked)
                && Objects.equals(c_balance_check, other.c_balance_check)
                && Objects.equals(c_changed, other.c_changed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(c_customer_id, c_account_number, c_account_id, c_ccy, c_filial_id, c_open_date, c_close_date,
                c_is_active, c_is_blocked_debit, c_is_blocked_credit, c_is_blocked, c_balance_check, c_changed);
    }

    @Override
    public String toString() {
        return "Account{" +
                ", c_customer_id=" + c_customer_id +
                ", c_account_id='" + c_account_id +
                ", c_account_number='" + c_account_number +
                ", c_ccy=" + c_ccy +
                ", c_filial_id=" + c_filial_id +
                ", c_open_date=" + c_open_date +
                ", c_close_date=" + c_close_date +
                ", c_is_active=" + c_is_active +
                ", c_is_blocked_debit=" + c_is_blocked_debit +
                ", c_is_blocked_credit=" + c_is_blocked_credit +
                ", c_balance_check=" + c_balance_check +
                ", c_is_blocked=" + c_is_blocked +
                ", c_changed=" + c_changed +
                '}';
    }

    private Map<String, Value<?>> toValue() {
        return Map.ofEntries(
            Map.entry("c_customer_id", c_customer_id == null ? VoidValue.of() : PrimitiveValue.newText(c_customer_id)),
            Map.entry("c_account_id", PrimitiveValue.newUuid(c_account_id)),
            Map.entry("c_account_number", PrimitiveValue.newText(c_account_number)),
            Map.entry("c_ccy", PrimitiveValue.newText(c_ccy)),
            Map.entry("c_filial_id", PrimitiveValue.newText(c_filial_id)),
            Map.entry("c_open_date", PrimitiveValue.newDate(c_open_date)),
            Map.entry("c_close_date", c_close_date == null ? VoidValue.of() : PrimitiveValue.newDate(c_close_date)),
            Map.entry("c_is_active", PrimitiveValue.newBool(c_is_active)),
            Map.entry("c_is_blocked_debit", PrimitiveValue.newBool(c_is_blocked_debit)),
            Map.entry("c_is_blocked_credit", PrimitiveValue.newBool(c_is_blocked_credit)),
            Map.entry("c_is_blocked", PrimitiveValue.newBool(c_is_blocked)),
            Map.entry("c_balance_check", PrimitiveValue.newText(c_balance_check)),
            Map.entry("c_changed", PrimitiveValue.newTimestamp(c_changed))
        );
    }

    public static ListValue toListValue(List<Account> items) {
        ListType listType = ListType.of(COLUMNS);
        return listType.newValue(items.stream()
                .map(e -> COLUMNS.newValue(e.toValue()))
                .collect(Collectors.toList()));
    }
}
