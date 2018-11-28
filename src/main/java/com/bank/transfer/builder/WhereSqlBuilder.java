package com.bank.transfer.builder;

import com.bank.transfer.dto.Filter;
import com.bank.transfer.dto.FilterType;

import java.util.List;
import java.util.Map;

public interface WhereSqlBuilder {
    OrderSqlBuilder where(List<Filter> filters);

    OrderSqlBuilder where(List<Filter> filters, Map<String, FilterType> fieldType);
}
