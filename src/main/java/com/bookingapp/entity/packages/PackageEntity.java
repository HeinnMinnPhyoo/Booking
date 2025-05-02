package com.bookingapp.entity.packages;

import jakarta.persistence.*;

@Entity
@Table(name = "packages")
public class PackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String countryCode;

    private int totalCredits;

    private double price;

    private int expiryDays;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getExpiryDays() {
        return expiryDays;
    }

    public void setExpiryDays(int expiryDays) {
        this.expiryDays = expiryDays;
    }
}
