package com.bank.transfer.dto;

import lombok.Value;

import java.util.List;

@Value
public class QueryParams {
    private List<Sort> sorts;
    private Pagination pagination;
    private List<Filter> filters;
}
