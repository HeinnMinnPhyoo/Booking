package com.bookingapp.packages.controller;

import com.bookingapp.packages.dto.PackageResponse;
import com.bookingapp.packages.dto.PurchaseRequest;
import com.bookingapp.packages.dto.UserPackageResponse;
import com.bookingapp.packages.service.PackageService;
import com.bookingapp.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/packages")
public class PackageController {

    @Autowired
    private PackageService packageService;
    @Autowired
    private UserService userService;

    @GetMapping("/available")
    public List<PackageResponse> getAvailable(@RequestParam String country) {
        return packageService.listAvailablePackages(country);
    }

    @PostMapping("/purchase")
    public String purchase(@RequestBody @Valid PurchaseRequest request, Authentication auth) {
        Long userId = userService.getUserByEmail(auth.getName()).getId();
        packageService.purchasePackage(userId, request);
        return "Purchase successful";
    }

    @GetMapping("/my")
    public List<UserPackageResponse> getMyPackages(Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        return packageService.getUserPackages(userId);
    }

    private Long getUserIdFromAuth(Authentication auth) {
        // Simplified; in production use a UserDetails object with ID
        return userService
                .getUserByEmail(auth.getName())
                .getId();
    }
}