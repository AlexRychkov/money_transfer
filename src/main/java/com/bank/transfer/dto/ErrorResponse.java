package com.bank.transfer.dto;

import lombok.Value;

import java.util.UUID;

@Value
public class ErrorResponse {
    private String message;

    public ErrorResponse(UUID uuid) {
        message = String.format("An error has occurred on server. ErrorResponse UUID: %s", uuid);
    }

    public ErrorResponse(String message) {
        this.message = message;
    }
}
