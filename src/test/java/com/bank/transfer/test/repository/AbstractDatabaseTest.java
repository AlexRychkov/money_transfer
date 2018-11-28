package com.bank.transfer.test.repository;

import com.bank.transfer.util.DatabaseUtil;
import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.jdbi.v3.core.Jdbi;
import org.junit.Before;

import java.io.IOException;
import java.net.URISyntaxException;

public abstract class AbstractDatabaseTest {
    protected EmbeddedPostgres pgServer;
    protected Jdbi jdbi;

    @Before
    public void before() throws IOException, URISyntaxException {
        pgServer = EmbeddedPostgres.start();
        jdbi = Jdbi.create(pgServer.getPostgresDatabase());
        DatabaseUtil.prepare(jdbi, "testSchemaAndData.sql");
    }
}