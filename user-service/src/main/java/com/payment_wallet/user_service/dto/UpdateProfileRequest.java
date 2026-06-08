package com.payment_wallet.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class UpdateProfileRequest {

    @Schema(description = "Updated first name", example = "Ada")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "Updated last name", example = "Lovelace")
    @NotBlank(message = "Last name is required")
    private String lastName;

    public UpdateProfileRequest() {}

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}
