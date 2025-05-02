package com.bookingapp.user.controller;

import com.bookingapp.entity.user.User;
import com.bookingapp.user.dto.*;
import com.bookingapp.user.service.AuthService;
import com.bookingapp.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired private UserService userService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody @Valid RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String email) {
        authService.verifyEmail(email);
        return "Email verified successfully.";
    }

    @GetMapping("/profile")
    public User getProfile(Authentication auth) {
        return userService.getUserByEmail(auth.getName());
    }

    @PutMapping("/password")
    public String changePassword(@RequestBody @Valid ChangePasswordRequest request, Authentication auth) {
        return userService.changePassword(auth.getName(), request);
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        return userService.resetPassword(request);
    }
}
