package com.diregebeya.backend.service.impl;

import com.diregebeya.backend.dto.coupon.CouponRequest;
import com.diregebeya.backend.dto.coupon.CouponResponse;
import com.diregebeya.backend.entity.Coupon;
import com.diregebeya.backend.exception.DuplicateResourceException;
import com.diregebeya.backend.exception.ResourceNotFoundException;
import com.diregebeya.backend.mapper.CouponMapper;
import com.diregebeya.backend.repository.CouponRepository;
import com.diregebeya.backend.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    @Override
    public List<CouponResponse> getAll() {
        return couponRepository.findAll().stream().map(couponMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public CouponResponse create(CouponRequest request) {
        if (couponRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new DuplicateResourceException(
                    "A coupon with code '%s' already exists".formatted(request.getCode()));
        }

        return couponMapper.toResponse(couponRepository.save(couponMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public CouponResponse update(Long id, CouponRequest request) {
        Coupon coupon = findByIdOrThrow(id);

        if (!coupon.getCode().equalsIgnoreCase(request.getCode())
                && couponRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new DuplicateResourceException(
                    "A coupon with code '%s' already exists".formatted(request.getCode()));
        }

        couponMapper.updateEntityFromRequest(request, coupon);
        return couponMapper.toResponse(coupon);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        couponRepository.delete(findByIdOrThrow(id));
    }

    private Coupon findByIdOrThrow(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Coupon", "id", id));
    }
}
