package com.bank.transfer.util;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;

import java.util.UUID;

@UtilityClass
public class LogUtil {
    public static UUID error(Logger log, Throwable t) {
        UUID uuid = UUID.randomUUID();
        log.error("ErrorResponse has occurred with UUID - {}. Stacktrace:", uuid, t);
        return uuid;
    }
}
