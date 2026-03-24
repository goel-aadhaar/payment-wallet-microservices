package com.payment_wallet.user_service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SignupRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String adminKey;
}
