package com.bank.transfer.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

@UtilityClass
public class ResourceUtil {
    static String readResource(String pathName) throws IOException {
        InputStream resourceStream = ResourceUtil.class.getClassLoader().getResourceAsStream(pathName);
        return IOUtils.toString(resourceStream, Charset.defaultCharset());
    }
}
