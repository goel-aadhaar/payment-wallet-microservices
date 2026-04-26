package com.payment_wallet.user_service.client;

import com.payment_wallet.user_service.dto.CreateWalletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WalletClient {

    private static final Logger log = LoggerFactory.getLogger(WalletClient.class);

    private final RestClient restClient;

    public WalletClient(@Value("${wallet.service.url:http://localhost:8083}") String walletServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(walletServiceUrl)
                .build();
    }

    public void createWallet(CreateWalletRequest request) {
        log.info("Creating wallet for userId: {}", request.getUserId());
        restClient.post()
                .uri("/api/v1/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
