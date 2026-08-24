package com.diregebeya.backend.service;

import com.diregebeya.backend.dto.product.ProductRequest;
import com.diregebeya.backend.dto.product.ProductResponse;
import com.diregebeya.backend.dto.product.ProductSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);

    ProductResponse getById(Long id);

    Page<ProductResponse> search(ProductSearchCriteria criteria, Pageable pageable);
}
