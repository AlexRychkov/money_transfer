package com.bank.transfer.builder;

import com.bank.transfer.dto.Pagination;

public interface PaginationSqlBuilder {
    String pagination(Pagination pagination);
}
