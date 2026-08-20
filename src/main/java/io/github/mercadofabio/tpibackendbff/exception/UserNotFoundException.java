package io.github.mercadofabio.tpibackendbff.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Usuario %d no existe".formatted(id));
    }
}
