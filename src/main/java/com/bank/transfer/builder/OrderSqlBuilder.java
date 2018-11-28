package com.bank.transfer.builder;

import com.bank.transfer.dto.Sort;

import java.util.List;

public interface OrderSqlBuilder {
    PaginationSqlBuilder order(List<Sort> sorts);
}
