package com.bank.transfer.validator;

import com.bank.transfer.exception.ValidationException;
import lombok.experimental.UtilityClass;

import static java.lang.String.format;
import static org.apache.commons.lang3.math.NumberUtils.isParsable;

@UtilityClass
public class NumericValidator {
    public static void validateNumeric(String value, String fieldName) {
        if (!isParsable(value)) {
            throw new ValidationException(format("Value \'%s\' for attribute \'%s\' has incorrect format", value, fieldName));
        }
    }
}
