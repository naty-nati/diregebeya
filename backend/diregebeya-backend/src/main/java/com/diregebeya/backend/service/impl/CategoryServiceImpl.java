package com.diregebeya.backend.service.impl;

import com.diregebeya.backend.dto.category.CategoryRequest;
import com.diregebeya.backend.dto.category.CategoryResponse;
import com.diregebeya.backend.entity.Category;
import com.diregebeya.backend.exception.DuplicateResourceException;
import com.diregebeya.backend.exception.ResourceNotFoundException;
import com.diregebeya.backend.mapper.CategoryMapper;
import com.diregebeya.backend.repository.CategoryRepository;
import com.diregebeya.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException(
                    "A category named '%s' already exists".formatted(request.getName()));
        }

        Category saved = categoryRepository.save(categoryMapper.toEntity(request));
        return categoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findByIdOrThrow(id);

        if (!category.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException(
                    "A category named '%s' already exists".formatted(request.getName()));
        }

        categoryMapper.updateEntityFromRequest(request, category);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = findByIdOrThrow(id);
        categoryRepository.delete(category);
    }

    @Override
    public CategoryResponse getById(Long id) {
        return categoryMapper.toResponse(findByIdOrThrow(id));
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    private Category findByIdOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Category", "id", id));
    }
}
