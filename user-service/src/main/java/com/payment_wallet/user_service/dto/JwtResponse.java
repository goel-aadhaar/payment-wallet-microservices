package com.payment_wallet.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class JwtResponse {

    @Schema(description = "JWT Bearer token for authentication", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String token;
    public JwtResponse() {}

    public JwtResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static JwtResponseBuilder builder() {
        return new JwtResponseBuilder();
    }

    public static class JwtResponseBuilder {
        private String token;
        public JwtResponseBuilder token(String token) {
            this.token = token;
            return this;
        }
        public JwtResponse build() {
            return new JwtResponse(token);
        }
    }
}
