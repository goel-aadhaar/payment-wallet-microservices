package com.payment_wallet.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {

    @Schema(description = "User's first name", example = "John")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(description = "User's email address (must be unique)", example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "A valid email address is required")
    private String email;

    @Schema(description = "User's password", example = "securepassword123")
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    public SignupRequest() {}

    public SignupRequest(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public static SignupRequestBuilder builder() {
        return new SignupRequestBuilder();
    }

    public static class SignupRequestBuilder {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        public SignupRequestBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }
        public SignupRequestBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }
        public SignupRequestBuilder email(String email) {
            this.email = email;
            return this;
        }
        public SignupRequestBuilder password(String password) {
            this.password = password;
            return this;
        }
        public SignupRequest build() {
            return new SignupRequest(firstName, lastName, email, password);
        }
    }
}
