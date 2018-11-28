package com.bank.transfer.dto;

public enum FilterOp {
    LT("<"),
    GT(">"),
    LTE("<="),
    GTE(">="),
    EQ("=");

    private final String op;

    FilterOp(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }
}
