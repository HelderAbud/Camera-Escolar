package com.faceblogai.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String MDC_REQUEST_ID = "requestId";
    public static final String MDC_USER = "user";

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (!StringUtils.hasText(requestId)) {
            requestId = UUID.randomUUID().toString();
        }

        Instant start = Instant.now();
        try (var ignored = MDC.putCloseable(MDC_REQUEST_ID, requestId)) {
            putUserIntoMdc();
            response.setHeader(REQUEST_ID_HEADER, requestId);

            filterChain.doFilter(request, response);
        } finally {
            long durationMs = Duration.between(start, Instant.now()).toMillis();

            // Obs: status pode ser setado no response durante o fluxo, por isso lemos no fim.
            int status = response.getStatus();
            MDC.put("httpMethod", request.getMethod());
            MDC.put("path", request.getRequestURI());
            MDC.put("status", String.valueOf(status));
            MDC.put("durationMs", String.valueOf(durationMs));

            log.info("request.completed");

            MDC.remove("httpMethod");
            MDC.remove("path");
            MDC.remove("status");
            MDC.remove("durationMs");
            MDC.remove(MDC_USER);
        }
    }

    private void putUserIntoMdc() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() != null) {
            String name = String.valueOf(auth.getPrincipal());
            if (StringUtils.hasText(name) && !"anonymousUser".equals(name)) {
                MDC.put(MDC_USER, name);
            }
        }
    }
}

