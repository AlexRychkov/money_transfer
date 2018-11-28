package com.bank.transfer.factory;

import com.bank.transfer.util.DatabaseUtil;
import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Singleton;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
@Factory
@Context
public class JdbiFactory {
    @Value("${pg.user}")
    private String pgUser;
    @Value("${pg.database}")
    private String pgDatabase;

    @Bean
    @Singleton
    public Jdbi createJdbi(EmbeddedPostgres pgServer) throws IOException, URISyntaxException {
        DataSource dataSource = pgServer.getDatabase(pgUser, pgDatabase);
        Jdbi jdbi = Jdbi.create(dataSource);
        DatabaseUtil.prepare(jdbi, "schema.sql");
        return jdbi;
    }
}