package io.github.mercadofabio.tpibackendbff.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import io.github.mercadofabio.tpibackendbff.client.ProductosClient;
import io.github.mercadofabio.tpibackendbff.client.UsuariosClient;
import io.github.mercadofabio.tpibackendbff.dto.ProductoDto;
import io.github.mercadofabio.tpibackendbff.dto.UsuarioDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OverviewServiceTest {

    @Mock
    private UsuariosClient usuariosClient;

    @Mock
    private ProductosClient productosClient;

    @InjectMocks
    private OverviewService overviewService;

    @Test
    void shouldBuildOverviewFromBothServices() {
        given(usuariosClient.getUsuarios()).willReturn(List.of(new UsuarioDto(1L, "Ana Lopez", "ADMIN")));
        given(productosClient.getProductos()).willReturn(List.of(new ProductoDto("P-100", "Notebook", 12)));

        var overview = overviewService.getOverview();

        assertThat(overview.totalUsuarios()).isEqualTo(1);
        assertThat(overview.totalProductos()).isEqualTo(1);
        assertThat(overview.usuarios()).extracting(UsuarioDto::nombre).containsExactly("Ana Lopez");
        assertThat(overview.productos()).extracting(ProductoDto::codigo).containsExactly("P-100");
    }
}
