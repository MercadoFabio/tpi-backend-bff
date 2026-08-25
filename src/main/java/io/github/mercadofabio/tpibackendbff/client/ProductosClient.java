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

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductosClient.class);
    private static final ParameterizedTypeReference<List<ProductoDto>> LIST_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestClient restClient;
    private final String productsUrl;

    public ProductosClient(@Qualifier("bffRestClientBuilder") RestClient.Builder restClientBuilder, ServiceProperties serviceProperties) {
        this.productsUrl = serviceProperties.getProductsUrl();
        this.restClient = restClientBuilder.baseUrl(this.productsUrl).build();
    }

    public List<ProductoDto> getProductos() {
        try {
            log.info("[BFF-UPSTREAM] 🚀 Calling Productos Microservice -> GET {}/productos", productsUrl);
            List<ProductoDto> result = Objects.requireNonNullElse(
                restClient.get().uri("/productos").retrieve().body(LIST_TYPE),
                List.of()
            );
            log.info("[BFF-UPSTREAM] 📥 Received {} products from Productos Microservice", result.size());
            return result;
        } catch (RestClientException exception) {
            log.error("[BFF-UPSTREAM] ❌ Failed to call Productos Microservice at {}/productos: {}", productsUrl, exception.getMessage());
            throw new UpstreamServiceException("products-service", exception);
        }
    }
}
