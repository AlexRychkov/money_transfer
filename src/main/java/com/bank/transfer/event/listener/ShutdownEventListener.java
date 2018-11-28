package com.bank.transfer.event.listener;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.ShutdownEvent;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Slf4j
@Singleton
public class ShutdownEventListener implements ApplicationEventListener<ShutdownEvent> {
    @Inject
    private EmbeddedPostgres pgServer;

    @Override
    public void onApplicationEvent(ShutdownEvent shutdownEvent) {
        try {
            pgServer.close();
            log.info("Postgres server was shut down");
        } catch (IOException e) {
            log.error("An error has occurred during stopping Postgres", e);
        }
    }
}
