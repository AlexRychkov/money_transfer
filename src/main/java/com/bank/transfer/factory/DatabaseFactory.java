package com.bank.transfer.factory;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;

import java.io.IOException;

@Factory
public class DatabaseFactory {
    @Value("${pg.port}")
    private Integer pgPort;

    @Bean
    @Context
    public EmbeddedPostgres pgServer() throws IOException {
        return EmbeddedPostgres.builder().setPort(pgPort).start();
    }
}
