package com.bookingapp.repository.userpackage;

import com.bookingapp.entity.userpackage.UserPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPackageRepository extends JpaRepository<UserPackage, Long> {
    List<UserPackage> findByUserId(Long userId);
    Optional<UserPackage> findTopByUserIdAndPack_CountryCodeAndExpiryDateAfterOrderByExpiryDateAsc(
            Long userId, String countryCode, java.time.LocalDateTime now);
}
