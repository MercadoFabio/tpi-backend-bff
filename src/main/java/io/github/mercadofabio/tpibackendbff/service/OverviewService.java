package io.github.mercadofabio.tpibackendbff.service;

import io.github.mercadofabio.tpibackendbff.client.ProductosClient;
import io.github.mercadofabio.tpibackendbff.client.UsuariosClient;
import io.github.mercadofabio.tpibackendbff.dto.OverviewResponse;
import io.github.mercadofabio.tpibackendbff.dto.ProductoDto;
import io.github.mercadofabio.tpibackendbff.dto.UsuarioDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OverviewService {

    private final UsuariosClient usuariosClient;
    private final ProductosClient productosClient;

    public OverviewService(UsuariosClient usuariosClient, ProductosClient productosClient) {
        this.usuariosClient = usuariosClient;
        this.productosClient = productosClient;
    }

    public OverviewResponse getOverview() {
        List<UsuarioDto> usuarios = usuariosClient.getUsuarios();
        List<ProductoDto> productos = productosClient.getProductos();
        return new OverviewResponse(usuarios, productos, usuarios.size(), productos.size());
    }
}
