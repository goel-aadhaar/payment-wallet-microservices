package com.payment_wallet.user_service.controller;

import com.payment_wallet.user_service.entity.User;
import com.payment_wallet.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    @PostMapping()
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUSerById(@PathVariable Long id) {
        return userService
                .getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<User> getAllUser() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body((User) userService.getAllUsers());
    }
}
