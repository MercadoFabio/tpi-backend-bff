package io.github.mercadofabio.tpibackendbff.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import io.github.mercadofabio.tpibackendbff.dto.LoginRequest;
import io.github.mercadofabio.tpibackendbff.service.DemoAuthenticationService;
import io.github.mercadofabio.tpibackendbff.service.CsrfTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.mock.web.MockHttpSession;
import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Import;

@WebMvcTest(controllers = SessionController.class)
@Import(CsrfTokenService.class)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DemoAuthenticationService authenticationService;

    @Test
    void shouldCreateServerSessionForValidCredentials() throws Exception {
        given(authenticationService.authenticate(new LoginRequest("profesor@example.edu", "un-password-largo")))
            .willReturn(true);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"profesor@example.edu\",\"password\":\"un-password-largo\"}"))
            .andExpect(status().isOk())
            .andExpect(cookie().httpOnly("__Host-tpi-session", true))
            .andExpect(cookie().secure("__Host-tpi-session", true))
            .andExpect(cookie().path("__Host-tpi-session", "/"))
            .andExpect(cookie().sameSite("__Host-tpi-session", "Strict"))
            .andExpect(cookie().exists("__Host-tpi-csrf"))
            .andExpect(jsonPath("$.authenticated").value(true));
    }

    @Test
    void shouldExposeCurrentSessionWithoutLeakingCredentials() throws Exception {
        mockMvc.perform(get("/api/v1/auth/session"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.authenticated").value(false))
            .andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test
    void shouldRequireCsrfTokenForLogout() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
            .andExpect(status().isForbidden());
    }

    @Test
    void shouldInvalidateSessionWhenCsrfTokenMatchesServerSession() throws Exception {
        given(authenticationService.authenticate(new LoginRequest("profesor@example.edu", "un-password-largo")))
            .willReturn(true);

        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"profesor@example.edu\",\"password\":\"un-password-largo\"}"))
            .andExpect(status().isOk())
            .andReturn();

        String csrf = login.getResponse().getCookie("__Host-tpi-csrf").getValue();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

        mockMvc.perform(post("/api/v1/auth/logout")
                .session(session)
                .cookie(new Cookie("__Host-tpi-csrf", csrf))
                .header("X-CSRF-Token", csrf))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldRejectInvalidCredentials() throws Exception {
        given(authenticationService.authenticate(new LoginRequest("profesor@example.edu", "un-password-largo")))
            .willReturn(false);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"profesor@example.edu\",\"password\":\"un-password-largo\"}"))
            .andExpect(status().isUnauthorized());
    }
}
