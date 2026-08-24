package io.github.mercadofabio.tpibackendbff.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import jakarta.annotation.PostConstruct;
import java.net.URI;

@ConfigurationProperties(prefix = "services")
public class ServiceProperties {

    private String usersUrl;
    private String productsUrl;

    public String getUsersUrl() {
        return usersUrl;
    }

    public void setUsersUrl(String usersUrl) {
        this.usersUrl = usersUrl;
    }

    public String getProductsUrl() {
        return productsUrl;
    }

    public void setProductsUrl(String productsUrl) {
        this.productsUrl = productsUrl;
    }

    @PostConstruct
    void validateDestinations() {
        validateUrl(usersUrl);
        validateUrl(productsUrl);
    }

    private void validateUrl(String value) {
        URI uri = URI.create(value);
        if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
            || uri.getHost() == null || uri.getUserInfo() != null || uri.getQuery() != null || uri.getFragment() != null) {
            throw new IllegalStateException("Invalid upstream service configuration");
        }
    }
}
