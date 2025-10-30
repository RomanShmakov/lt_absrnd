package JointImport.Tables;

import tech.ydb.table.values.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


/*
-- Клиенты
CREATE TABLE `main/t_customer` (
  c_customer_id utf8 NOT NULL,
  -- Тип клиента
  c_type utf8 NOT NULL,
  -- Категория клиента
  c_category utf8 NOT NULL,
  -- Наименование
  c_name utf8 NOT NULL,
  -- Дата открытия
  c_open_date date NOT NULL,
  -- Дата закрытия
  c_close_date date,
  -- Резидентность
  c_resdnt utf8 NOT NULL,
  -- ИНН
  c_inn utf8,
  -- Время изменения
  c_changed timestamp NOT NULL,
  PRIMARY KEY (c_customer_id)
) WITH (
  AUTO_PARTITIONING_BY_SIZE = ENABLED,
  AUTO_PARTITIONING_BY_LOAD = ENABLED,
  AUTO_PARTITIONING_MIN_PARTITIONS_COUNT = 40,
  AUTO_PARTITIONING_MAX_PARTITIONS_COUNT = 100,
  AUTO_PARTITIONING_PARTITION_SIZE_MB = 1024
);
 */

public class Customer {

    public static final StructType COLUMNS = StructType.of(
            Map.ofEntries(
                    Map.entry("c_customer_id", PrimitiveType.Text),
                    Map.entry("c_type", PrimitiveType.Text),
                    Map.entry("c_category", PrimitiveType.Text),
                    Map.entry("c_name", PrimitiveType.Text),
                    Map.entry("c_open_date", PrimitiveType.Date),
                    Map.entry("c_close_date", PrimitiveType.Date),
                    Map.entry("c_resdnt", PrimitiveType.Text),
                    Map.entry("c_inn", PrimitiveType.Text),
                    Map.entry("c_changed", PrimitiveType.Timestamp)
            )
    );

    public static final List<String> PRIMARY_KEYS = Arrays.asList("c_customer_id");

    private final String c_customer_id;
    private final String c_type;
    private final String c_category;
    private final String c_name;
    private final Instant c_open_date;
    private final Instant c_close_date;
    private final String c_resdnt;
    private final String c_inn;
    private final Instant c_changed;

    public Customer(String c_customer_id, String c_type, String c_category, String c_name, Instant c_open_date,
                    Instant c_close_date, String c_resdnt, String c_inn, Instant c_changed) {
        this.c_customer_id = c_customer_id;
        this.c_type = c_type;
        this.c_category = c_category;
        this.c_name = c_name;
        this.c_open_date = c_open_date;
        this.c_close_date = c_close_date;
        this.c_resdnt = c_resdnt;
        this.c_inn = c_inn;
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

        Customer other = (Customer) o;

        return Objects.equals(c_customer_id, other.c_customer_id)
                && Objects.equals(c_type, other.c_type)
                && Objects.equals(c_category, other.c_category)
                && Objects.equals(c_name, other.c_name)
                && Objects.equals(c_open_date, other.c_open_date)
                && Objects.equals(c_close_date, other.c_close_date)
                && Objects.equals(c_resdnt, other.c_resdnt)
                && Objects.equals(c_inn, other.c_inn)
                && Objects.equals(c_changed, other.c_changed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(c_customer_id, c_type, c_category, c_name, c_open_date, c_close_date, c_resdnt, c_inn, c_changed);
    }

    @Override
    public String toString() {
        return "Customer{" +
                ", c_customer_id='" + c_customer_id +
                ", c_type='" + c_type +
                ", c_category='" + c_category +
                ", c_name='" + c_name +
                ", c_open_date='" + c_open_date +
                ", c_close_date='" + c_close_date +
                ", c_resdnt=" + c_resdnt +
                ", c_inn=" + c_inn +
                ", c_changed=" + c_changed +
                '}';
    }


    private Map<String, Value<?>> toValue() {
        return Map.ofEntries(
                Map.entry("c_customer_id", PrimitiveValue.newText(c_customer_id)),
                Map.entry("c_type", PrimitiveValue.newText(c_type)),
                Map.entry("c_category", PrimitiveValue.newText(c_category)),
                Map.entry("c_name", PrimitiveValue.newText(c_name)),
                Map.entry("c_open_date", PrimitiveValue.newDate(c_open_date)),
                Map.entry("c_close_date", c_close_date == null ? VoidValue.of() : PrimitiveValue.newDate(c_close_date)),
                Map.entry("c_resdnt", PrimitiveValue.newText(c_resdnt)),
                Map.entry("c_inn", c_inn == null ? VoidValue.of() : PrimitiveValue.newText(c_inn)),
                Map.entry("c_changed", PrimitiveValue.newTimestamp(c_changed))
        );
    }

    public static ListValue toListValue(List<Customer> items) {
        ListType listType = ListType.of(COLUMNS);
        return listType.newValue(items.stream()
                .map(e -> COLUMNS.newValue(e.toValue()))
                .collect(Collectors.toList()));
    }
}
