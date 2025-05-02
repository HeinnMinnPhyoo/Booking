package com.bookingapp.packages.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PackageResponse {
    private String name;
    private String countryCode;
    private int credits;
    private double price;
    private int expiryDays;
}
