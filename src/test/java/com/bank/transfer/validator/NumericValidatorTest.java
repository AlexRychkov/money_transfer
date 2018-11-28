package com.bank.transfer.validator;

import com.bank.transfer.exception.ValidationException;
import org.junit.Test;

public class NumericValidatorTest {
    @Test
    public void validateNumericOK1() {
        NumericValidator.validateNumeric("0.00", "dfd");
    }

    @Test
    public void validateNumericOK2() {
        NumericValidator.validateNumeric(".00", "dfd");
    }

    @Test
    public void validateNumericOK3() {
        NumericValidator.validateNumeric("3423", "dfd");
    }

    @Test
    public void validateNumericOK4() {
        NumericValidator.validateNumeric("3423.546456", "dfd");
    }

    @Test(expected = ValidationException.class)
    public void validateNumericLetter() {
        NumericValidator.validateNumeric("342d3.546456", "dfd");
    }
}