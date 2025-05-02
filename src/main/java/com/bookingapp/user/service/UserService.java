package com.bookingapp.user.service;

import com.bookingapp.entity.user.User;
import com.bookingapp.repository.user.UserRepository;
import com.bookingapp.user.dto.ChangePasswordRequest;
import com.bookingapp.user.dto.ResetPasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public String changePassword(String email, ChangePasswordRequest req) {
        User user = getUserByEmail(email);
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect old password.");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        return "Password changed.";
    }

    public String resetPassword(ResetPasswordRequest req) {
        User user = getUserByEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        return "Password reset (mocked).";
    }
}
