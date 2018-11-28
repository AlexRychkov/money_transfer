package com.bank.transfer.dto;

import lombok.Value;

@Value
public class Sort {
    private String field;
    private SortOrder order;
}
