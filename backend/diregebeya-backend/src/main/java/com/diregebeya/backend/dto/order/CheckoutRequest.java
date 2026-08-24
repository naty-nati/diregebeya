package com.diregebeya.backend.dto.order;

import com.diregebeya.backend.entity.PaymentMethod;
import com.diregebeya.backend.entity.ShippingAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotNull(message = "Shipping address is required")
    @Valid
    private ShippingAddress shippingAddress;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    /** Optional - null/blank means no coupon applied. */
    private String couponCode;
}
