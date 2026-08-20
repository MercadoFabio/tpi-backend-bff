package io.github.mercadofabio.tpibackendbff.exception;

public class UpstreamServiceException extends RuntimeException {

    public UpstreamServiceException(String serviceName, Throwable cause) {
        super("No se pudo consultar %s".formatted(serviceName), cause);
    }
}
