package com.diregebeya.backend.service;

import com.diregebeya.backend.dto.coupon.CouponRequest;
import com.diregebeya.backend.dto.coupon.CouponResponse;

import java.util.List;

/** Admin-only in every method - coupon codes and redemption stats are internal. */
public interface CouponService {

    List<CouponResponse> getAll();

    CouponResponse create(CouponRequest request);

    CouponResponse update(Long id, CouponRequest request);

    void delete(Long id);
}
