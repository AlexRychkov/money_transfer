package com.bank.transfer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Filter {
    private String field;
    private FilterOp op;
    private String value;
    private FilterType type;

    public Filter(String field, FilterOp op, String value) {
        this.field = field;
        this.op = op;
        this.value = value;
    }

    public Filter(Filter filter, FilterType type) {
        this.field = filter.field;
        this.op = filter.op;
        this.value = filter.value;
        this.type = type;
    }
}
