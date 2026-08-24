package io.github.mercadofabio.tpibackendbff.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CsrfTokenService {

    public static final String COOKIE_NAME = "__Host-tpi-csrf";
    private static final String SESSION_ATTRIBUTE = "csrfToken";
    private static final String HEADER_NAME = "X-CSRF-Token";
    private final SecureRandom secureRandom = new SecureRandom();

    public void issue(HttpSession session, HttpServletResponse response) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(SESSION_ATTRIBUTE, token);
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(COOKIE_NAME, token)
            .httpOnly(false)
            .secure(true)
            .path("/")
            .sameSite("Strict")
            .build()
            .toString());
    }

    public boolean isValid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object expected = session.getAttribute(SESSION_ATTRIBUTE);
        String cookieValue = readCookie(request, COOKIE_NAME);
        String headerValue = request.getHeader(HEADER_NAME);
        if (!(expected instanceof String) || cookieValue == null || headerValue == null) {
            return false;
        }
        return constantTimeEquals((String) expected, cookieValue)
            && constantTimeEquals(cookieValue, headerValue);
    }

    private String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private boolean constantTimeEquals(String left, String right) {
        return MessageDigest.isEqual(left.getBytes(java.nio.charset.StandardCharsets.UTF_8),
            right.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
