package com.bartek.ecommerce.service;

import com.bartek.ecommerce.dto.ProductDto;

import java.util.List;

public interface ProductService {

    ProductDto createProduct(ProductDto productDto);
    ProductDto updateProduct(Long productId, ProductDto productDto);
    ProductDto getProductById(Long productId);
    List<ProductDto> getAllProducts();
    List<ProductDto> getAllProductsForAdmin();
    List<ProductDto> getProductsByCategory(Long categoryId);
    List<ProductDto> getArchivedProducts();
    List<ProductDto> searchProduct(String searchValue);
    void archiveProduct(Long productId);
    //void deleteProduct(Long productId);
    void restoreProduct(Long productId);
}
