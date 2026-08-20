package io.github.mercadofabio.tpibackendbff.client;

import io.github.mercadofabio.tpibackendbff.config.ServiceProperties;
import io.github.mercadofabio.tpibackendbff.dto.UsuarioDto;
import io.github.mercadofabio.tpibackendbff.exception.UpstreamServiceException;
import io.github.mercadofabio.tpibackendbff.exception.UserNotFoundException;
import java.util.List;
import java.util.Objects;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class UsuariosClient {

    private static final ParameterizedTypeReference<List<UsuarioDto>> LIST_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestClient restClient;

    public UsuariosClient(RestClient.Builder restClientBuilder, ServiceProperties serviceProperties) {
        this.restClient = restClientBuilder.baseUrl(serviceProperties.getUsersUrl()).build();
    }

    public List<UsuarioDto> getUsuarios() {
        try {
            return Objects.requireNonNullElse(
                restClient.get().uri("/usuarios").retrieve().body(LIST_TYPE),
                List.of()
            );
        } catch (RestClientException exception) {
            throw new UpstreamServiceException("users-service", exception);
        }
    }

    public UsuarioDto getUsuarioById(Long id) {
        try {
            return restClient.get().uri("/usuarios/{id}", id).retrieve().body(UsuarioDto.class);
        } catch (HttpClientErrorException.NotFound exception) {
            throw new UserNotFoundException(id);
        } catch (RestClientException exception) {
            throw new UpstreamServiceException("users-service", exception);
        }
    }
}
