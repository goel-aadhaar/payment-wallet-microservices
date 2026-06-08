package com.payment_wallet.api_gateway.controller;

import com.payment_wallet.common.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Set;

@RestController
public class ProxyController {

    // JDK HttpClient-based factory: unlike the default, it supports PATCH.
    private final RestTemplate restTemplate = new RestTemplate(new JdkClientHttpRequestFactory());
    private final JwtUtil jwtUtil;

    @Value("${USER_SERVICE_URL:http://user-service:8081}")
    private String userServiceUrl;

    @Value("${WALLET_SERVICE_URL:http://wallet-service:8088}")
    private String walletServiceUrl;

    @Value("${TRANSACTION_SERVICE_URL:http://transaction-service:8082}")
    private String transactionServiceUrl;

    @Value("${REWARD_SERVICE_URL:http://reward-service:8084}")
    private String rewardServiceUrl;

    @Value("${NOTIFICATION_SERVICE_URL:http://notification-service:8083}")
    private String notificationServiceUrl;

    public ProxyController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @RequestMapping(value = {"/auth/**", "/api/v1/users/**"}, method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
    public ResponseEntity<?> proxyToUserService(HttpServletRequest request, @RequestBody(required = false) byte[] body) {
        return forward(userServiceUrl, request, body);
    }

    @RequestMapping(value = "/api/v1/wallets/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
    public ResponseEntity<?> proxyToWalletService(HttpServletRequest request, @RequestBody(required = false) byte[] body) {
        return forward(walletServiceUrl, request, body);
    }

    @RequestMapping(value = "/api/v1/transactions/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
    public ResponseEntity<?> proxyToTransactionService(HttpServletRequest request, @RequestBody(required = false) byte[] body) {
        return forward(transactionServiceUrl, request, body);
    }

    @RequestMapping(value = "/api/v1/rewards/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
    public ResponseEntity<?> proxyToRewardService(HttpServletRequest request, @RequestBody(required = false) byte[] body) {
        return forward(rewardServiceUrl, request, body);
    }

    @RequestMapping(value = "/api/v1/notifications/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
    public ResponseEntity<?> proxyToNotificationService(HttpServletRequest request, @RequestBody(required = false) byte[] body) {
        return forward(notificationServiceUrl, request, body);
    }

    private ResponseEntity<?> forward(String baseUrl, HttpServletRequest request, byte[] body) {
        String path = request.getRequestURI();

        // Authenticate every non-auth route by validating the bearer token.
        if (!path.startsWith("/auth/")) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }
            try {
                jwtUtil.validateToken(authHeader.substring(7));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT validation failed: " + e.getMessage());
            }
        }

        // Forward the request to the downstream service.
        String query = request.getQueryString();
        String url = baseUrl + path + (query != null ? "?" + query : "");

        // Skip hop-by-hop headers that break downstream connections
        Set<String> skipHeaders = Set.of("host", "content-length", "transfer-encoding", "connection");

        HttpHeaders headers = new HttpHeaders();
        Collections.list(request.getHeaderNames()).forEach(headerName -> {
            if (!skipHeaders.contains(headerName.toLowerCase())) {
                headers.addAll(headerName, Collections.list(request.getHeaders(headerName)));
            }
        });

        HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<byte[]> resp = restTemplate.exchange(
                    url, HttpMethod.valueOf(request.getMethod()), entity, byte[].class);
            return relayResponse(resp.getStatusCode(), resp.getHeaders(), resp.getBody());
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            return relayResponse(e.getStatusCode(), e.getResponseHeaders(), e.getResponseBodyAsByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(("Downstream service unavailable: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Relays a downstream response, stripping hop-by-hop/framing headers. Copying the downstream
     * Content-Length / Transfer-Encoding verbatim corrupts the re-sent response (e.g. a chunked
     * error body arrives with a conflicting framing and the client never receives it), so we let
     * the gateway's own container reframe the body.
     */
    private ResponseEntity<byte[]> relayResponse(HttpStatusCode status, HttpHeaders downstream, byte[] body) {
        Set<String> skip = Set.of("transfer-encoding", "content-length", "connection");
        HttpHeaders clean = new HttpHeaders();
        if (downstream != null) {
            downstream.forEach((name, values) -> {
                if (!skip.contains(name.toLowerCase())) {
                    clean.addAll(name, values);
                }
            });
        }
        return ResponseEntity.status(status).headers(clean).body(body);
    }
}
