package com.diregebeya.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Embedded (not its own table) - a shipping address only ever exists as part
 * of the order it was captured on, and is never queried independently.
 * Carries jakarta.validation annotations directly since CheckoutRequest
 * validates it inline via {@code @Valid} rather than through a separate DTO.
 */
@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {

    @NotBlank(message = "Full name is required")
    @Column(nullable = false, length = 150)
    private String fullName;

    @NotBlank(message = "Phone is required")
    @Column(nullable = false, length = 30)
    private String phone;

    @NotBlank(message = "Address line 1 is required")
    @Column(nullable = false, length = 200)
    private String addressLine1;

    @Column(length = 200)
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Column(nullable = false, length = 100)
    private String city;

    @NotBlank(message = "Region is required")
    @Column(nullable = false, length = 100)
    private String region;

    @Column(length = 20)
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Column(nullable = false, length = 100)
    private String country;
}
