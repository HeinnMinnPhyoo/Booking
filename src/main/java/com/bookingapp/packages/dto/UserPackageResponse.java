package com.bookingapp.packages.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserPackageResponse {
    private String name;
    private String countryCode;
    private int remainingCredits;
    private boolean expired;
    private LocalDateTime expiryDate;
}
