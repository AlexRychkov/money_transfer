package com.bank.transfer.builder;

import com.bank.transfer.dto.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class SqlBuilder implements WhereSqlBuilder, OrderSqlBuilder, PaginationSqlBuilder {
    private StringBuilder query;

    private SqlBuilder(String tableName) {
        query = new StringBuilder(300);
        query.append("select * from ").append(tableName);
    }

    public static WhereSqlBuilder query(String tableName) {
        return new SqlBuilder(tableName);
    }

    public static String of(String tableName, QueryParams params) {
        return SqlBuilder.query(tableName)
                .where(params.getFilters())
                .order(params.getSorts())
                .pagination(params.getPagination());
    }

    @Override
    public OrderSqlBuilder where(List<Filter> filters) {
        return where(filters, this::resolveValueAndType);
    }

    @Override
    public OrderSqlBuilder where(List<Filter> filters, Map<String, FilterType> fieldType) {
        return where(filters, (filter) -> resolveValueAndType(filter, fieldType));
    }

    private OrderSqlBuilder where(List<Filter> filters, Function<Filter, String> valueSupplier) {
        if (filters.isEmpty()) {
            return this;
        }
        query.append(" where ")
                .append(filters.stream()
                        .map(filter -> filter.getField().concat(" ")
                                .concat(filter.getOp().getOp())
                                .concat(" ").concat(valueSupplier.apply(filter))
                        )
                        .reduce((a, b) -> a.concat(" and ").concat(b)).get());
        return this;
    }

    private String resolveValueAndType(Filter filter) {
        if (filter.getType() != null) {
            return "'".concat(filter.getValue()).concat("'::").concat(filter.getType().getPostfix());
        } else {
            return filter.getValue();
        }
    }

    private String resolveValueAndType(Filter filter, Map<String, FilterType> fieldType) {
        FilterType filterType = fieldType.get(filter.getField());
        if (filterType != null) {
            filter = new Filter(filter, filterType);
        }
        return resolveValueAndType(filter);
    }

    @Override
    public String pagination(Pagination pagination) {
        String limit = String.valueOf(pagination.getLimit());
        query.append(" limit ").append(limit);
        if (pagination.getOffset() != 0) {
            String offset = String.valueOf(pagination.getOffset());
            query.append(" offset ").append(offset);
        }
        return query.toString();
    }

    @Override
    public SqlBuilder order(List<Sort> sorts) {
        if (sorts.isEmpty()) {
            return this;
        }
        query.append(" order by ")
                .append(sorts.stream()
                        .map(sort -> sort.getField().concat(" ")
                                .concat(sort.getOrder().toString().toLowerCase()))
                        .reduce((a, b) -> a.concat(", ").concat(b)).get());
        return this;
    }
}