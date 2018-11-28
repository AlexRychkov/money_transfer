package com.bank.transfer.controller.handler;

import com.bank.transfer.dto.ErrorResponse;
import com.bank.transfer.exception.ValidationException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {ValidationException.class, ExceptionHandler.class})
public class ValidationExceptionHandler implements ExceptionHandler<ValidationException, HttpResponse> {
    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest httpRequest, ValidationException validationException) {
        return HttpResponse.<ErrorResponse>badRequest().body(new ErrorResponse(validationException.getMessage()));
    }
}