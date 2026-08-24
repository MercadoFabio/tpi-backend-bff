package io.github.mercadofabio.tpibackendbff.service;

import io.github.mercadofabio.tpibackendbff.config.DemoAuthProperties;
import io.github.mercadofabio.tpibackendbff.dto.LoginRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class DemoAuthenticationService {

    private final DemoAuthProperties properties;

    public DemoAuthenticationService(DemoAuthProperties properties) {
        this.properties = properties;
    }

    public boolean authenticate(LoginRequest request) {
        if (isBlank(properties.getEmail()) || isBlank(properties.getPassword())) {
            return false;
        }
        return matches(properties.getEmail(), request.email()) && matches(properties.getPassword(), request.password());
    }

    private boolean matches(String expected, String actual) {
        return MessageDigest.isEqual(
            expected.getBytes(StandardCharsets.UTF_8),
            actual.getBytes(StandardCharsets.UTF_8)
        );
    }

    private boolean isBlank(String value) {
        return Objects.isNull(value) || value.isBlank();
    }
}
