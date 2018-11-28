package com.bank.transfer.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class TypeUtil {
    public BigDecimal convert(String value){
        return new BigDecimal(value.replace(",", "").replace("$", ""));
    }
}
