package com.diregebeya.backend.service.impl;

import com.diregebeya.backend.dto.product.ProductRequest;
import com.diregebeya.backend.dto.product.ProductResponse;
import com.diregebeya.backend.dto.product.ProductSearchCriteria;
import com.diregebeya.backend.entity.Category;
import com.diregebeya.backend.entity.Product;
import com.diregebeya.backend.exception.ResourceNotFoundException;
import com.diregebeya.backend.mapper.ProductMapper;
import com.diregebeya.backend.repository.CategoryRepository;
import com.diregebeya.backend.repository.ProductRepository;
import com.diregebeya.backend.service.ProductService;
import com.diregebeya.backend.specification.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        product.setCategory(findCategoryOrThrow(request.getCategoryId()));

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findByIdOrThrow(id);

        productMapper.updateEntityFromRequest(request, product);
        if (!request.getCategoryId().equals(product.getCategory().getId())) {
            product.setCategory(findCategoryOrThrow(request.getCategoryId()));
        }

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = findByIdOrThrow(id);
        productRepository.delete(product);
    }

    @Override
    public ProductResponse getById(Long id) {
        return productMapper.toResponse(findByIdOrThrow(id));
    }

    @Override
    public Page<ProductResponse> search(ProductSearchCriteria criteria, Pageable pageable) {
        Specification<Product> spec = ProductSpecifications.combine(
                ProductSpecifications.hasCategory(criteria.categoryId()),
                ProductSpecifications.hasBrand(criteria.brand()),
                ProductSpecifications.priceGreaterThanOrEqualTo(criteria.minPrice()),
                ProductSpecifications.priceLessThanOrEqualTo(criteria.maxPrice()),
                ProductSpecifications.nameOrBrandContains(criteria.search()));

        return productRepository.findAll(spec, pageable).map(productMapper::toResponse);
    }

    private Product findByIdOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", "id", id));
    }

    private Category findCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> ResourceNotFoundException.of("Category", "id", categoryId));
    }
}
