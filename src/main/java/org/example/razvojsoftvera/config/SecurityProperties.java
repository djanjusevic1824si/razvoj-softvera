package org.example.razvojsoftvera.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        String jwtSecret,
        Duration jwtTrajanje,
        List<String> corsOrigins) {
}
