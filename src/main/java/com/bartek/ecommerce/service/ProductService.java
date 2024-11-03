package com.bartek.ecommerce.service;

import com.bartek.ecommerce.dto.ProductDto;

import java.util.List;

public interface ProductService {

    ProductDto createProduct(ProductDto productDto, Long categoryId);
    ProductDto updateProduct(ProductDto productDto, Long categoryId);
    ProductDto getProductById(Long productId);
    List<ProductDto> getAllProducts();
    List<ProductDto> getProductsByCategory(Long categoryId);
    List<ProductDto> searchProduct(String searchValue);
    void deleteProduct(Long productId);
}
