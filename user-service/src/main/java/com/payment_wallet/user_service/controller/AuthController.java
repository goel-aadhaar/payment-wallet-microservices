package com.payment_wallet.user_service.controller;

import com.payment_wallet.user_service.dto.JwtResponse;
import com.payment_wallet.user_service.dto.LoginRequest;
import com.payment_wallet.user_service.dto.SignupRequest;
import com.payment_wallet.user_service.entity.User;
import com.payment_wallet.user_service.repository.UserRepository;
import com.payment_wallet.user_service.util.JwtUtil;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Data
@Builder
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .role("ROLE_USER")
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if(userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }

        User user = userOpt.get();

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid Credentials");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());

        String token = jwtUtil.generateToken(claims, user.getEmail());

        return ResponseEntity.status(HttpStatus.OK).body(new JwtResponse(token));
    }


}