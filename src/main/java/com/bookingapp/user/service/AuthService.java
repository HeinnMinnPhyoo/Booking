package com.bookingapp.user.service;

import com.bookingapp.entity.user.User;
import com.bookingapp.security.provider.JwtTokenProvider;
import com.bookingapp.user.dto.AuthResponse;
import com.bookingapp.user.dto.LoginRequest;
import com.bookingapp.user.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bookingapp.repository.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCountryCode(request.getCountryCode());
        user.setVerified(true); // or false if you plan to verify later

        User saved = userRepository.save(user);

        //adding payment card (assuming you get card details in the registration request)
        /*if (registrationRequest.getCardNumber() != null) { // Adjust based on your DTO
            boolean cardAdded = paymentService.addPaymentCard(
                    registrationRequest.getCardNumber(),
                    registrationRequest.getExpiryDate(),
                    registrationRequest.getCvv(),
                    userService.getCurrentUserId() // You'll need a way to get the user ID
            );
            if (!cardAdded) {
                // Handle add payment card failure
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration successful, but failed to add payment card.");
            }
        }*/

        // Generate JWT
        String token = jwtTokenProvider.generateToken(saved.getEmail());

        return new AuthResponse(saved.getId(), saved.getEmail(), token);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationCredentialsNotFoundException("Invalid email or password");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        String token = jwtTokenProvider.generateToken(user.getEmail());

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                token
        );
    }

    public boolean verifyEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerified(true);
        userRepository.save(user);
        return true;
    }

    // Mock function
    private boolean SendVerifyEmail(String email) {
        // Simulate success
        return true;
    }
}
