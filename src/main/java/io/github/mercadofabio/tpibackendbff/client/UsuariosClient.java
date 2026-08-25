package io.github.mercadofabio.tpibackendbff.client;

import io.github.mercadofabio.tpibackendbff.config.ServiceProperties;
import io.github.mercadofabio.tpibackendbff.dto.UsuarioDto;
import io.github.mercadofabio.tpibackendbff.exception.UpstreamServiceException;
import io.github.mercadofabio.tpibackendbff.exception.UserNotFoundException;
import java.util.List;
import java.util.Objects;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class UsuariosClient {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UsuariosClient.class);
    private static final ParameterizedTypeReference<List<UsuarioDto>> LIST_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestClient restClient;
    private final String usersUrl;

    public UsuariosClient(@Qualifier("bffRestClientBuilder") RestClient.Builder restClientBuilder, ServiceProperties serviceProperties) {
        this.usersUrl = serviceProperties.getUsersUrl();
        this.restClient = restClientBuilder.baseUrl(this.usersUrl).build();
    }

    public List<UsuarioDto> getUsuarios() {
        try {
            log.info("[BFF-UPSTREAM] 🚀 Calling Usuarios Microservice -> GET {}/usuarios", usersUrl);
            List<UsuarioDto> result = Objects.requireNonNullElse(
                restClient.get().uri("/usuarios").retrieve().body(LIST_TYPE),
                List.of()
            );
            log.info("[BFF-UPSTREAM] 📥 Received {} users from Usuarios Microservice", result.size());
            return result;
        } catch (RestClientException exception) {
            log.error("[BFF-UPSTREAM] ❌ Failed to call Usuarios Microservice at {}/usuarios: {}", usersUrl, exception.getMessage());
            throw new UpstreamServiceException("users-service", exception);
        }
    }

    public UsuarioDto getUsuarioById(Long id) {
        try {
            log.info("[BFF-UPSTREAM] 🚀 Calling Usuarios Microservice -> GET {}/usuarios/{}", usersUrl, id);
            UsuarioDto result = restClient.get().uri("/usuarios/{id}", id).retrieve().body(UsuarioDto.class);
            log.info("[BFF-UPSTREAM] 📥 Received user #{} ({}) from Usuarios Microservice", id, result != null ? result.nombre() : "null");
            return result;
        } catch (HttpClientErrorException.NotFound exception) {
            log.warn("[BFF-UPSTREAM] ⚠️ User #{} not found in Usuarios Microservice", id);
            throw new UserNotFoundException(id);
        } catch (RestClientException exception) {
            log.error("[BFF-UPSTREAM] ❌ Failed to call Usuarios Microservice for user #{}: {}", id, exception.getMessage());
            throw new UpstreamServiceException("users-service", exception);
        }
    }
}
