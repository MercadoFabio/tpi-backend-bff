package io.github.mercadofabio.tpibackendbff.controller;

import io.github.mercadofabio.tpibackendbff.client.ProductosClient;
import io.github.mercadofabio.tpibackendbff.client.UsuariosClient;
import io.github.mercadofabio.tpibackendbff.dto.OverviewResponse;
import io.github.mercadofabio.tpibackendbff.dto.ProductoDto;
import io.github.mercadofabio.tpibackendbff.dto.UsuarioDto;
import io.github.mercadofabio.tpibackendbff.service.OverviewService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BffController {

    private final UsuariosClient usuariosClient;
    private final ProductosClient productosClient;
    private final OverviewService overviewService;

    public BffController(UsuariosClient usuariosClient, ProductosClient productosClient, OverviewService overviewService) {
        this.usuariosClient = usuariosClient;
        this.productosClient = productosClient;
        this.overviewService = overviewService;
    }

    @GetMapping("/usuarios")
    public List<UsuarioDto> getUsuarios() {
        return usuariosClient.getUsuarios();
    }

    @GetMapping("/usuarios/{id}")
    public UsuarioDto getUsuarioById(@PathVariable Long id) {
        return usuariosClient.getUsuarioById(id);
    }

    @GetMapping("/productos")
    public List<ProductoDto> getProductos() {
        return productosClient.getProductos();
    }

    @GetMapping("/overview")
    public OverviewResponse getOverview() {
        return overviewService.getOverview();
    }
}
