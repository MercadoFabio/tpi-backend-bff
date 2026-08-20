package io.github.mercadofabio.tpibackendbff.dto;

public record ApiErrorDto(int status, String message, String path) {
}
