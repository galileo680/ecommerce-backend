package com.galileo.ecommerce.catalog.application;

import com.galileo.ecommerce.catalog.domain.Category;
import com.galileo.ecommerce.catalog.infrastructure.CategoryRepository;
import com.galileo.ecommerce.catalog.infrastructure.ProductRepository;
import com.galileo.ecommerce.common.domain.BusinessRuleException;
import com.galileo.ecommerce.common.domain.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categories;
    private final ProductRepository products;

    public UUID create(String name, UUID parentId) {
        if (parentId != null && !categories.existsById(parentId)) {
            throw new ResourceNotFoundException("category %s not found".formatted(parentId));
        }
        requireFreeSlug(Category.slugOf(name));
        return categories.save(Category.create(name, parentId)).getId();
    }

    public void rename(UUID id, String name) {
        Category category = requireCategory(id);
        String newSlug = Category.slugOf(name);
        if (!newSlug.equals(category.getSlug())) {
            requireFreeSlug(newSlug);
        }
        category.rename(name);
    }

    public void delete(UUID id) {
        Category category = requireCategory(id);
        if (categories.existsByParentId(id)) {
            throw new BusinessRuleException("a category with subcategories cannot be deleted");
        }
        if (products.existsByCategoryId(id)) {
            throw new BusinessRuleException("a category with products cannot be deleted");
        }
        categories.delete(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryNode> tree() {
        List<Category> all = categories.findAll();
        Map<UUID, List<Category>> byParent = all.stream()
            .filter(category -> category.getParentId() != null)
            .collect(Collectors.groupingBy(Category::getParentId));
        return all.stream()
            .filter(category -> category.getParentId() == null)
            .map(category -> toNode(category, byParent))
            .toList();
    }

    private CategoryNode toNode(Category category, Map<UUID, List<Category>> byParent) {
        List<CategoryNode> children = byParent.getOrDefault(category.getId(), List.of()).stream()
            .map(child -> toNode(child, byParent))
            .toList();
        return new CategoryNode(category.getId(), category.getName(), category.getSlug(), children);
    }

    private Category requireCategory(UUID id) {
        return categories.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("category %s not found".formatted(id)));
    }

    private void requireFreeSlug(String slug) {
        if (categories.existsBySlug(slug)) {
            throw new BusinessRuleException("category slug %s is already used".formatted(slug));
        }
    }
}
