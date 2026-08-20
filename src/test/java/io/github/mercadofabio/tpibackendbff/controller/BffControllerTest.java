package io.github.mercadofabio.tpibackendbff.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.mercadofabio.tpibackendbff.client.ProductosClient;
import io.github.mercadofabio.tpibackendbff.client.UsuariosClient;
import io.github.mercadofabio.tpibackendbff.dto.OverviewResponse;
import io.github.mercadofabio.tpibackendbff.dto.ProductoDto;
import io.github.mercadofabio.tpibackendbff.dto.UsuarioDto;
import io.github.mercadofabio.tpibackendbff.exception.GlobalExceptionHandler;
import io.github.mercadofabio.tpibackendbff.exception.UserNotFoundException;
import io.github.mercadofabio.tpibackendbff.service.OverviewService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BffController.class)
@Import(GlobalExceptionHandler.class)
class BffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuariosClient usuariosClient;

    @MockBean
    private ProductosClient productosClient;

    @MockBean
    private OverviewService overviewService;

    @Test
    void shouldReturnOverview() throws Exception {
        OverviewResponse response = new OverviewResponse(
            List.of(new UsuarioDto(1L, "Ana Lopez", "ADMIN")),
            List.of(new ProductoDto("P-100", "Notebook", 12)),
            1,
            1
        );
        given(overviewService.getOverview()).willReturn(response);

        mockMvc.perform(get("/api/overview"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalUsuarios").value(1))
            .andExpect(jsonPath("$.totalProductos").value(1))
            .andExpect(jsonPath("$.usuarios[0].nombre").value("Ana Lopez"))
            .andExpect(jsonPath("$.productos[0].codigo").value("P-100"));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        given(usuariosClient.getUsuarioById(99L)).willThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/api/usuarios/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("Usuario 99 no existe"));
    }
}
