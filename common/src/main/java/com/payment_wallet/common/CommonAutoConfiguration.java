package com.payment_wallet.common;

import com.payment_wallet.common.error.GlobalExceptionHandler;
import com.payment_wallet.common.security.JwtUtil;
import com.payment_wallet.common.web.CorrelationIdFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration that wires the shared cross-cutting beans into any service that depends on
 * this library — without requiring the service to component-scan {@code com.payment_wallet.common}.
 * Registered via {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports}.
 */
@AutoConfiguration
public class CommonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public CorrelationIdFilter correlationIdFilter() {
        return new CorrelationIdFilter();
    }

    /** Only created for services that define a {@code jwt.secret} (gateway, user-service). */
    @Bean
    @ConditionalOnProperty(prefix = "jwt", name = "secret")
    @ConditionalOnMissingBean
    public JwtUtil jwtUtil(@Value("${jwt.secret}") String secret,
                           @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        return new JwtUtil(secret, expirationMs);
    }
}
