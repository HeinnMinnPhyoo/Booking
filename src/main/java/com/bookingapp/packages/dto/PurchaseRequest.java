package com.bookingapp.packages.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseRequest {
    @NotNull
    private Long packageId;
}
