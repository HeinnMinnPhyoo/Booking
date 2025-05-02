package com.bookingapp.entity.userpackage;

import com.bookingapp.entity.packages.PackageEntity;
import com.bookingapp.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_packages")
public class UserPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private PackageEntity pack;

    private int remainingCredits;

    private LocalDateTime purchaseDate;

    private LocalDateTime expiryDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PackageEntity getPack() {
        return pack;
    }

    public void setPack(PackageEntity pack) {
        this.pack = pack;
    }

    public int getRemainingCredits() {
        return remainingCredits;
    }

    public void setRemainingCredits(int remainingCredits) {
        this.remainingCredits = remainingCredits;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
