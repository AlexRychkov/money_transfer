package com.bank.transfer.controller.handler;

import com.bank.transfer.dto.ErrorResponse;
import com.bank.transfer.util.LogUtil;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.postgresql.util.PSQLException;

import javax.inject.Singleton;
import java.util.UUID;

import static io.micronaut.http.HttpStatus.BAD_REQUEST;
import static io.micronaut.http.HttpStatus.CONFLICT;

@Slf4j
@Produces
@Singleton
@Requires(classes = {Exception.class, ExceptionHandler.class})
public class UnableExecuteStatementExceptionHandler implements ExceptionHandler<UnableToExecuteStatementException, HttpResponse> {
    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest httpRequest, UnableToExecuteStatementException exception) {
        ErrorType errorType = parseException(exception);
        switch (errorType) {
            case DUPLICATE:
                return HttpResponse.<ErrorResponse>status(CONFLICT).body(new ErrorResponse("Entity already exist"));
            case COLUMN_NOT_EXIST:
                return HttpResponse.<ErrorResponse>status(BAD_REQUEST).body(new ErrorResponse("Check your request. Incorrect attribute was found"));
        }
        UUID uuid = LogUtil.error(log, exception);
        return HttpResponse.<ErrorResponse>serverError().body(new ErrorResponse(uuid));
    }

    private ErrorType parseException(Exception exception) {
        String message = exception.getMessage();
        if (exception.getCause() !=null && exception.getCause().getClass().equals(PSQLException.class)) {
            if (message.contains("duplicate")) {
                return ErrorType.DUPLICATE;
            } else if (message.contains("ERROR: column") && message.contains("does not exist")) {
                return ErrorType.COLUMN_NOT_EXIST;
            }
        }
        return ErrorType.OTHER_ERROR;
    }

    private enum ErrorType {
        DUPLICATE,
        COLUMN_NOT_EXIST,
        OTHER_ERROR
    }
}
