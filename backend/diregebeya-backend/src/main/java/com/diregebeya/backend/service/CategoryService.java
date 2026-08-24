package com.diregebeya.backend.service;

import com.diregebeya.backend.dto.category.CategoryRequest;
import com.diregebeya.backend.dto.category.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    void delete(Long id);

    CategoryResponse getById(Long id);

    List<CategoryResponse> getAll();
}
