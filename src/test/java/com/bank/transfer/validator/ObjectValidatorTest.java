package com.bank.transfer.validator;

import com.bank.transfer.exception.ValidationException;
import org.junit.Test;

public class ObjectValidatorTest {
    @Test
    public void nonNullOK() {
        ObjectValidator.nonNull(new Object(), "");
    }

    @Test(expected = ValidationException.class)
    public void nonNullThrowException() {
        ObjectValidator.nonNull(null, "");
    }

    @Test
    public void testOK() {
        ObjectValidator.test(() -> true, "");
    }

    @Test(expected = ValidationException.class)
    public void testThrowException() {
        ObjectValidator.test(() -> false, "");
    }
}