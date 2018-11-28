package com.bank.transfer.controller.handler;

import com.bank.transfer.dto.ErrorResponse;
import com.bank.transfer.exception.EntityNotFoundException;
import io.micronaut.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import static io.micronaut.http.HttpStatus.NOT_FOUND;
import static org.junit.Assert.assertEquals;

public class EntityNotFoundExceptionHandlerTest {
    EntityNotFoundExceptionHandler handler;

    @Before
    public void before() {
        handler = new EntityNotFoundExceptionHandler();
    }

    @Test
    public void handleNotFoundStatus() {
        HttpResponse<ErrorResponse> response = handler.handle(null, new EntityNotFoundException("A", "1"));
        assertEquals(response.status(), NOT_FOUND);
    }
}