package com.bank.transfer.controller.handler;

import com.bank.transfer.dto.ErrorResponse;
import io.micronaut.http.HttpResponse;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

import static io.micronaut.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;

public class UnableExecuteStatementExceptionHandlerTest {
    UnableExecuteStatementExceptionHandler handler;

    @Before
    public void before() {
        handler = new UnableExecuteStatementExceptionHandler();
    }

    @Test
    public void testHandleConflict() {
        UnableToExecuteStatementException exception = new UnableToExecuteStatementException("db error has occurred with duplicate entity");
        exception.initCause(new PSQLException(new ServerErrorMessage("error")));
        HttpResponse<ErrorResponse> response = handler
                .handle(null, exception);
        assertEquals(CONFLICT, response.status());
    }

    @Test
    public void testHandleBadRequestCauseNotExistingColumn() {
        UnableToExecuteStatementException exception = new UnableToExecuteStatementException("ERROR: column \"sfsdf\" does not exist");
        exception.initCause(new PSQLException(new ServerErrorMessage("ERROR: column \"sfsdf\" does not exist")));
        HttpResponse<ErrorResponse> response = handler.handle(null, exception);
        assertEquals(BAD_REQUEST, response.status());
    }

    @Test
    public void testHandleOther() {
        HttpResponse<ErrorResponse> response = handler
                .handle(null, new UnableToExecuteStatementException("some other error with some message"));
        assertEquals(INTERNAL_SERVER_ERROR, response.status());
    }
}