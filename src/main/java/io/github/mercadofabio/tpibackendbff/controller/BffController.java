package io.github.mercadofabio.tpibackendbff.controller;

import io.github.mercadofabio.tpibackendbff.client.ProductosClient;
import io.github.mercadofabio.tpibackendbff.client.UsuariosClient;
import io.github.mercadofabio.tpibackendbff.dto.OverviewResponse;
import io.github.mercadofabio.tpibackendbff.dto.ProductoDto;
import io.github.mercadofabio.tpibackendbff.dto.UsuarioDto;
import io.github.mercadofabio.tpibackendbff.service.OverviewService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class BffController {

    private static final Logger log = LoggerFactory.getLogger(BffController.class);
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
        log.info("[BFF-ROUTER] 🌐 GET /api/v1/usuarios -> Routing request to Usuarios Microservice");
        List<UsuarioDto> list = usuariosClient.getUsuarios();
        log.info("[BFF-ROUTER] 👤 Returning {} users to frontend", list.size());
        return list;
    }

    @GetMapping("/usuarios/{id}")
    public UsuarioDto getUsuarioById(@PathVariable Long id) {
        log.info("[BFF-ROUTER] 🌐 GET /api/v1/usuarios/{} -> Routing to Usuarios Microservice", id);
        return usuariosClient.getUsuarioById(id);
    }

    @GetMapping("/productos")
    public List<ProductoDto> getProductos() {
        log.info("[BFF-ROUTER] 🌐 GET /api/v1/productos -> Routing request to Productos Microservice");
        List<ProductoDto> list = productosClient.getProductos();
        log.info("[BFF-ROUTER] 📦 Returning {} products to frontend", list.size());
        return list;
    }

    @GetMapping("/overview")
    public OverviewResponse getOverview() {
        log.info("[BFF-ROUTER] 📊 GET /api/v1/overview -> Aggregating data from multiple microservices");
        return overviewService.getOverview();
    }
}
