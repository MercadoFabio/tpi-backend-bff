package io.github.mercadofabio.tpibackendbff.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiSessionFilter extends OncePerRequestFilter {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ApiSessionFilter.class);
    public static final String SESSION_EMAIL_ATTRIBUTE = "authenticatedEmail";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/v1/") || path.startsWith("/api/v1/auth/");
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getSession(false) == null
            || request.getSession(false).getAttribute(SESSION_EMAIL_ATTRIBUTE) == null) {
            log.warn("[BFF-SECURITY] 🚫 Blocked unauthenticated request: {} {} (Missing __Host-tpi-session cookie)", request.getMethod(), request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"message\":\"Authentication is required\"}");
            return;
        }
        Object userEmail = request.getSession(false).getAttribute(SESSION_EMAIL_ATTRIBUTE);
        log.info("[BFF-SECURITY] 🛡️ Authorized request: {} {} (User: {})", request.getMethod(), request.getRequestURI(), userEmail);
        filterChain.doFilter(request, response);
    }
}
