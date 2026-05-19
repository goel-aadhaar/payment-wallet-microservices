package com.payment_wallet.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginRequest {

    @Schema(description = "Registered email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's password", example = "securepassword123")
    private String password;
    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static LoginRequestBuilder builder() {
        return new LoginRequestBuilder();
    }

    public static class LoginRequestBuilder {
        private String email;
        private String password;
        public LoginRequestBuilder email(String email) {
            this.email = email;
            return this;
        }
        public LoginRequestBuilder password(String password) {
            this.password = password;
            return this;
        }
        public LoginRequest build() {
            return new LoginRequest(email, password);
        }
    }
}
