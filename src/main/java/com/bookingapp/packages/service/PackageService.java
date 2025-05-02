package com.bookingapp.packages.service;


import com.bookingapp.entity.packages.PackageEntity;
import com.bookingapp.entity.user.User;
import com.bookingapp.entity.userpackage.UserPackage;
import com.bookingapp.packages.dto.PackageResponse;
import com.bookingapp.packages.dto.PurchaseRequest;
import com.bookingapp.packages.dto.UserPackageResponse;
import com.bookingapp.repository.packages.PackageRepository;
import com.bookingapp.repository.user.UserRepository;
import com.bookingapp.repository.userpackage.UserPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;
    @Autowired
    private UserPackageRepository userPackageRepository;
    @Autowired
    private UserRepository userRepository;

    public List<PackageResponse> listAvailablePackages(String country) {
        return packageRepository.findByCountryCode(country).stream()
                .map(p -> new PackageResponse(p.getName(), p.getCountryCode(), p.getTotalCredits(), p.getPrice(), p.getExpiryDays()))
                .collect(Collectors.toList());
    }

    public void purchasePackage(Long userId, PurchaseRequest request) {
        PackageEntity pack = packageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new RuntimeException("Package not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean paid = mockPayment();
        if (!paid) throw new RuntimeException("Payment failed");

        LocalDateTime now = LocalDateTime.now();
        UserPackage up = new UserPackage();
        up.setUser(user);
        up.setPack(pack);
        up.setPurchaseDate(now);
        up.setRemainingCredits(pack.getTotalCredits());
        up.setExpiryDate(now.plusDays(pack.getExpiryDays()));

        userPackageRepository.save(up);
    }

    public List<UserPackageResponse> getUserPackages(Long userId) {
        return userPackageRepository.findByUserId(userId).stream()
                .map(up -> new UserPackageResponse(
                        up.getPack().getName(),
                        up.getPack().getCountryCode(),
                        up.getRemainingCredits(),
                        up.getExpiryDate().isBefore(LocalDateTime.now()),
                        up.getExpiryDate()
                )).collect(Collectors.toList());
    }

    // Mocked
    private boolean mockPayment() {
        return true;
    }
}
