package com.bank.transfer;

import io.micronaut.runtime.Micronaut;

import java.io.IOException;
import java.net.URISyntaxException;

public class Application {
    public static void main(String[] args) throws IOException, URISyntaxException {
        Micronaut.run(Application.class);
    }
}