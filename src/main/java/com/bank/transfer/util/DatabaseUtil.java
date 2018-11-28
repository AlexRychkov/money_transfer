package com.bank.transfer.util;

import org.jdbi.v3.core.Jdbi;

import java.io.IOException;
import java.net.URISyntaxException;

public class DatabaseUtil {
    public static void prepare(Jdbi jdbi, String schemaLocation) throws IOException {
        String query = ResourceUtil.readResource(schemaLocation);
        jdbi.useHandle(handle -> handle.execute(query));
    }
}
