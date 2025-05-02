package com.bookingapp.repository.packages;

import com.bookingapp.entity.packages.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<PackageEntity, Long> {
    List<PackageEntity> findByCountryCode(String countryCode);
}
