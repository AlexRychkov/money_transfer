package com.bank.transfer.mapper;

import com.bank.transfer.dto.*;
import com.bank.transfer.exception.ValidationException;
import com.bank.transfer.validator.NumericValidator;
import io.micronaut.core.convert.value.ConvertibleMultiValues;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.compress.utils.Lists.newArrayList;

@UtilityClass
public class QueryParamsMapper {
    /**
     * Parse query params like 'sort=asc(accountId),desc(customerId)&balance=gte:100,lt:1000&limit=20&offset=1000' to
     * QueryParams object.
     * Sort syntax: sort=[asc(]fieldName[)]
     * Filter syntax: fieldName=op:value
     * Limit/offset syntax: limit/offset=value
     *
     * @param httpParams params from request
     * @return QueryParams with details of sorting, pagination, filtering
     * @throws com.bank.transfer.exception.ValidationException if query string has syntax errors
     * @throws com.bank.transfer.exception.ValidationException if query have few limit/offset values
     * @throws com.bank.transfer.exception.ValidationException if query in sort expr has incorrect format
     * @throws com.bank.transfer.exception.ValidationException if query in filter expr has incorrect format
     */
    public static QueryParams map(ConvertibleMultiValues<String> httpParams) {
        val pagination = new Pagination();
        List<Filter> filters = newArrayList();
        List<Sort> sorts = newArrayList();
        for (val httpParam : httpParams) {
            val paramName = httpParam.getKey();
            val paramValue = httpParam.getValue();
            if (paramName.equals("sort")) {
                addToList(sorts, parseSorts(paramValue));
            } else if (paramName.equals("limit") || paramName.equals("offset")) {
                parsePagination(pagination, paramName, paramValue);
            } else {
                addToList(filters, parseFilter(paramName, paramValue));
            }
        }
        return new QueryParams(sorts, pagination, filters);
    }

    private static <T> void addToList(List<T> list, List<T> values) {
        if (values.size() == 1) {
            list.add(values.get(0));
        } else {
            list.addAll(values);
        }
    }

    private static List<Sort> parseSorts(Collection<String> values) {
        return values.stream()
                .map(QueryParamsMapper::split)
                .flatMap(Stream::of)
                .map(QueryParamsMapper::parseSort).collect(toList());
    }

    private static Sort parseSort(String sortValue) {
        if (sortValue.startsWith("asc(") && sortValue.endsWith(")")) {
            return new Sort(sortValue.substring(4, sortValue.length() - 1), SortOrder.ASC);
        } else if (sortValue.startsWith("desc(") && sortValue.endsWith(")")) {
            return new Sort(sortValue.substring(5, sortValue.length() - 1), SortOrder.DESC);
        } else if (isIncorrectSortExpr(sortValue)) {
            throw new ValidationException(format("Sort value '%s' has incorrect format", sortValue));
        } else {
            return new Sort(sortValue, SortOrder.ASC);
        }
    }

    private static boolean isIncorrectSortExpr(String expr) {
        return (expr.contains("asc") || expr.contains("desc")) &&
                !(expr.contains("(") && expr.contains(")"));
    }

    private static void parsePagination(Pagination pagination, String paramName, List<String> values) {
        if (values.size() != 1 || split(values.get(0)).length > 1) {
            throw new ValidationException("Query string has incorrect format (few limit/offset values)");
        }
        NumericValidator.validateNumeric(values.get(0), "limit/offset");
        int numericValue = Integer.parseInt(values.get(0));
        if (paramName.equals("limit")) {
            pagination.setLimit(numericValue);
        } else if (paramName.equals("offset")) {
            pagination.setOffset(numericValue);
        }
    }

    private static List<Filter> parseFilter(String field, Collection<String> values) {
        return values.stream()
                .map(QueryParamsMapper::split)
                .flatMap(Stream::of)
                .map(value -> {
                    String[] opAndVal = value.split(":");
                    if (opAndVal.length != 2) {
                        throw new ValidationException(format("Filter value '%s' have incorrect syntax", value));
                    }
                    FilterOp op;
                    try {
                        op = FilterOp.valueOf(opAndVal[0].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new ValidationException(format("Filter operator '%s' have incorrect syntax", opAndVal[0]), e);
                    }
                    return new Filter(field, op, opAndVal[1]);
                }).collect(toList());
    }

    private static String[] split(String value) {
        return value.split(",");
    }
}