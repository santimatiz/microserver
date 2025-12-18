package com.smatiz.microrest;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigTest {

    @Test
    public void testConfig() throws IOException {
        File configFile = File.createTempFile("config", ".properties");
        FileWriter writer = new FileWriter(configFile);
        writer.write("con_url=jdbc:postgresql://localhost/test\n");
        writer.write("db_user=user\n");
        writer.write("db_password=password\n");
        writer.write("port=8080\n");
        writer.write("timeout=2000\n");
        writer.write("ssl=false\n");
        writer.write("log=INFO\n");
        writer.close();

        Config config = new Config(configFile.getAbsolutePath());

        assertEquals("jdbc:postgresql://localhost/test", config.getConnectionUrl());
        assertEquals("user", config.getDb_user());
        assertEquals("password", config.getDb_password());
        assertEquals(8080, config.getPort());
        assertEquals(2000, config.getTime_out());
        assertEquals(false, config.isSsl());
        assertEquals("INFO", config.getLog());
    }
}
