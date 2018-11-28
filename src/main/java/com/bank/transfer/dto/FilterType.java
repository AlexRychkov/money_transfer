package com.bank.transfer.dto;

public enum FilterType {
    MONEY("money"),
    STRING("text");

    private final String postfix;

    FilterType(String postfix) {
        this.postfix = postfix;
    }

    public String getPostfix() {
        return postfix;
    }
}
