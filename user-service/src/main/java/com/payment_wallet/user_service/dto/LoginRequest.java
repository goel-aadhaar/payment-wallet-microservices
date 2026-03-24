package com.payment_wallet.user_service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LoginRequest {

    private String email;

    private String password;
}
