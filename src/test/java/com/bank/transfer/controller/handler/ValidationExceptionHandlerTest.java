package com.bank.transfer.controller.handler;

import com.bank.transfer.dto.ErrorResponse;
import com.bank.transfer.exception.ValidationException;
import io.micronaut.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import static io.micronaut.http.HttpStatus.BAD_REQUEST;
import static org.junit.Assert.assertEquals;

public class ValidationExceptionHandlerTest {
    ValidationExceptionHandler handler;

    @Before
    public void setUp() {
        handler = new ValidationExceptionHandler();
    }

    @Test
    public void handle() {
        HttpResponse<ErrorResponse> response = handler.handle(null, new ValidationException(""));
        assertEquals(response.status(), BAD_REQUEST);
    }
}