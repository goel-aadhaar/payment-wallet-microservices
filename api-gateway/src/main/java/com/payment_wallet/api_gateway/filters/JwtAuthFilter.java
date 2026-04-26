package com.payment_wallet.api_gateway.filters;

import com.payment_wallet.api_gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;

@Component
public class JwtAuthFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/signup",
            "/auth/login"
    );

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {

        String path = request.path();

        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            return next.handle(request);
        }

        String authHeader = request.headers().firstHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ServerResponse.status(401).build();
        }

        try {
            String token = authHeader.substring(7);

            Claims claims = JwtUtil.validateToken(token);

            request.attributes().put("X-User-Email", claims.getSubject());
            request.attributes().put("X-User-Id", claims.get("userId"));
            request.attributes().put("X-User-Role", claims.get("role"));

            log.debug("JWT authentication successful for user: {}", claims.getSubject());

            return next.handle(request);

        } catch (Exception e) {
            log.warn("JWT authentication failed: {}", e.getMessage());
            return ServerResponse.status(401).build();
        }
    }
}