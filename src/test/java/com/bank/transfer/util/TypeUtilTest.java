package com.bank.transfer.util;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class TypeUtilTest {
    @Test
    public void testDollar() {
        assertEquals(new BigDecimal("0.00"), TypeUtil.convert("$0.00"));
    }

    @Test
    public void testDollarAndComma() {
        assertEquals(new BigDecimal("34343.35"), TypeUtil.convert("$34,343.35"));
    }

    @Test
    public void testSimple() {
        assertEquals(new BigDecimal("23523525"), TypeUtil.convert("23523525"));
    }

    @Test
    public void testWithFraction() {
        assertEquals(new BigDecimal("100.10"), TypeUtil.convert("100.10"));
    }

    @Test
    public void testWithLongFraction() {
        assertEquals(new BigDecimal("100.10234234"), TypeUtil.convert("100.10234234"));
    }
}