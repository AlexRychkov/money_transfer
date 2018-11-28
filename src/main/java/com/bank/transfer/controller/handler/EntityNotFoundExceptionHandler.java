package com.bank.transfer.controller.handler;

import com.bank.transfer.dto.ErrorResponse;
import com.bank.transfer.exception.EntityNotFoundException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {EntityNotFoundException.class, ExceptionHandler.class})
public class EntityNotFoundExceptionHandler implements ExceptionHandler<EntityNotFoundException, HttpResponse> {
    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest httpRequest, EntityNotFoundException notFoundException) {
        return HttpResponse.<ErrorResponse>notFound().body(new ErrorResponse(notFoundException.getMessage()));
    }
}