package io.github.mercadofabio.tpibackendbff.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SessionResponse(boolean authenticated, String email) {
}
