package com.payment_wallet.user_service.service;

import com.payment_wallet.common.error.ResourceNotFoundException;
import com.payment_wallet.user_service.client.WalletClient;
import com.payment_wallet.user_service.dto.CreateWalletRequest;
import com.payment_wallet.user_service.dto.UpdateProfileRequest;
import com.payment_wallet.user_service.entity.User;
import com.payment_wallet.user_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final WalletClient walletClient;

    public UserServiceImpl(UserRepository userRepository, WalletClient walletClient) {
        this.userRepository = userRepository;
        this.walletClient = walletClient;
    }

    @Override
    @Transactional
    public User createUser(User user) {
        User saved = userRepository.save(user);

        try {
            CreateWalletRequest request = new CreateWalletRequest();
            request.setUserId(saved.getId());
            request.setCurrency("INR");
            walletClient.createWallet(request);
            log.info("Wallet created for user: {}", saved.getId());
        } catch (Exception e) {
            log.error("Wallet creation failed for user: {}, rolling back", saved.getId(), e);
            userRepository.deleteById(saved.getId());
            throw new RuntimeException("Wallet creation failed: " + e.getMessage(), e);
        }
        return saved;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateProfile(Long id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        return userRepository.save(user);
    }
}
