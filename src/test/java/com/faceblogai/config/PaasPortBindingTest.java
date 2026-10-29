package com.faceblogai.config;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class PaasPortBindingTest {

    @Test
    void serverPortFollowsPortWithLocalDefault8082() throws Exception {
        String raw = rawServerPort();

        assertThat(raw).isEqualTo("${PORT:8082}");
        assertThat(resolve(raw, null)).isEqualTo("8082");
        assertThat(resolve(raw, "4321")).isEqualTo("4321");
    }

    private static String rawServerPort() throws Exception {
        Properties properties = new Properties();
        try (InputStream in = new ClassPathResource("application.properties").getInputStream()) {
            properties.load(in);
        }
        return properties.getProperty("server.port");
    }

    private static String resolve(String raw, String port) {
        PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}", ":", false);
        return helper.replacePlaceholders(raw, key -> "PORT".equals(key) ? port : null);
    }
}
