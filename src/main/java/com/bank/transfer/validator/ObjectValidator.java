package com.bank.transfer.validator;

import com.bank.transfer.exception.ValidationException;
import lombok.experimental.UtilityClass;

import java.util.function.Predicate;
import java.util.function.Supplier;

@UtilityClass
public class ObjectValidator {
    public static void nonNull(Object object, String fieldName) {
        if (object == null) {
            throw new ValidationException(String.format("Attribute \'%s\' is required", fieldName));
        }
    }

    public static void test(Supplier<Boolean> validator, String message) {
        if (!validator.get()) {
            throw new ValidationException(message);
        }
    }
}
