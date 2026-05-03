package com.payment_wallet.api_gateway.config;

import org.springframework.cloud.gateway.server.mvc.filter.Bucket4jFilterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.time.Duration;
import java.util.function.Function;
import org.springframework.web.servlet.function.ServerRequest;

@Configuration
public class RateLimitConfig {

    /**
     * Key resolver that identifies users by their User-Id header/attribute,
     * falling back to IP address if not available.
     */
    @Bean
    public Function<ServerRequest, String> userKeyResolver() {
        return request -> {
            // Check for X-User-Id header
            String userId = request.headers().firstHeader("X-User-Id");
            if (userId == null) {
                userId = request.headers().firstHeader("X_User-Id");
            }

            // Check if JwtAuthFilter already put it in attributes
            if (userId == null) {
                Object attrId = request.attribute("X-User-Id").orElse(null);
                if (attrId != null) {
                    userId = attrId.toString();
                }
            }

            if (userId != null && !userId.isEmpty()) {
                return userId;
            }

            // Fallback to remote address
            return request.remoteAddress()
                    .map(addr -> addr.getAddress().getHostAddress())
                    .orElse("anonymous");
        };
    }

    /**
     * Rate limit filter: 10 requests per second, burst capacity of 20.
     */
    @Bean
    public HandlerFilterFunction<ServerResponse, ServerResponse> rateLimitFilter(
            Function<ServerRequest, String> userKeyResolver) {
        return Bucket4jFilterFunctions.rateLimit(c -> c
                .setCapacity(20)
                .setPeriod(Duration.ofSeconds(1))
                .setKeyResolver(userKeyResolver)
        );
    }
}
