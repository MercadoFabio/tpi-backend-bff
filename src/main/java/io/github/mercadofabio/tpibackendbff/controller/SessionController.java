package io.github.mercadofabio.tpibackendbff.controller;

import io.github.mercadofabio.tpibackendbff.dto.LoginRequest;
import io.github.mercadofabio.tpibackendbff.dto.SessionResponse;
import io.github.mercadofabio.tpibackendbff.security.ApiSessionFilter;
import io.github.mercadofabio.tpibackendbff.service.DemoAuthenticationService;
import io.github.mercadofabio.tpibackendbff.service.CsrfTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class SessionController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SessionController.class);
    private final DemoAuthenticationService authenticationService;
    private final CsrfTokenService csrfTokenService;

    public SessionController(DemoAuthenticationService authenticationService, CsrfTokenService csrfTokenService) {
        this.authenticationService = authenticationService;
        this.csrfTokenService = csrfTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<SessionResponse> login(
        @Valid @RequestBody LoginRequest request,
        HttpServletRequest servletRequest,
        HttpServletResponse servletResponse
    ) {
        log.info("[BFF-AUTH] 🔐 POST /api/v1/auth/login - Processing login for user: {}", request.email());
        if (!authenticationService.authenticate(request)) {
            log.warn("[BFF-AUTH] ❌ Invalid credentials for user: {}", request.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        servletRequest.getSession(true);
        servletRequest.changeSessionId();
        String sessionId = servletRequest.getSession(false).getId();
        servletRequest.getSession(false).setAttribute(ApiSessionFilter.SESSION_EMAIL_ATTRIBUTE, request.email());
        Cookie sessionCookie = new Cookie("__Host-tpi-session", sessionId);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true);
        sessionCookie.setPath("/");
        sessionCookie.setAttribute("SameSite", "Strict");
        servletResponse.addCookie(sessionCookie);
        csrfTokenService.issue(servletRequest.getSession(false), servletResponse);
        log.info("[BFF-AUTH] ✅ Login successful! User: {}, Session ID created in Redis: {}", request.email(), sessionId);
        return ResponseEntity.ok(new SessionResponse(true, request.email()));
    }

    @org.springframework.web.bind.annotation.GetMapping("/session")
    public SessionResponse currentSession(HttpServletRequest request) {
        if (request.getSession(false) == null) {
            log.info("[BFF-AUTH] 🔍 GET /api/v1/auth/session - No active session found");
            return new SessionResponse(false, null);
        }
        Object email = request.getSession(false).getAttribute(ApiSessionFilter.SESSION_EMAIL_ATTRIBUTE);
        boolean active = email instanceof String;
        log.info("[BFF-AUTH] 🔍 GET /api/v1/auth/session - Session active: {}, user: {}", active, email);
        return active ? new SessionResponse(true, (String) email) : new SessionResponse(false, null);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        if (!csrfTokenService.isValid(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        Cookie expiredSession = new Cookie("__Host-tpi-session", "");
        expiredSession.setHttpOnly(true);
        expiredSession.setSecure(true);
        expiredSession.setPath("/");
        expiredSession.setMaxAge(0);
        expiredSession.setAttribute("SameSite", "Strict");
        response.addCookie(expiredSession);
        return ResponseEntity.noContent().build();
    }
}
