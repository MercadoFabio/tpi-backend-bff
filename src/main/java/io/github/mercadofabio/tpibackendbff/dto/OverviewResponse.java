package io.github.mercadofabio.tpibackendbff.dto;

import java.util.List;

public record OverviewResponse(
    List<UsuarioDto> usuarios,
    List<ProductoDto> productos,
    int totalUsuarios,
    int totalProductos
) {
}
