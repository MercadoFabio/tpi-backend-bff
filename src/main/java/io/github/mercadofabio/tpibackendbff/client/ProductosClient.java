package io.github.mercadofabio.tpibackendbff.client;

import io.github.mercadofabio.tpibackendbff.config.ServiceProperties;
import io.github.mercadofabio.tpibackendbff.dto.ProductoDto;
import io.github.mercadofabio.tpibackendbff.exception.UpstreamServiceException;
import java.util.List;
import java.util.Objects;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ProductosClient {

    private static final ParameterizedTypeReference<List<ProductoDto>> LIST_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestClient restClient;

    public ProductosClient(@Qualifier("bffRestClientBuilder") RestClient.Builder restClientBuilder, ServiceProperties serviceProperties) {
        this.restClient = restClientBuilder.baseUrl(serviceProperties.getProductsUrl()).build();
    }

    public List<ProductoDto> getProductos() {
        try {
            return Objects.requireNonNullElse(
                restClient.get().uri("/productos").retrieve().body(LIST_TYPE),
                List.of()
            );
        } catch (RestClientException exception) {
            throw new UpstreamServiceException("products-service", exception);
        }
    }
}
