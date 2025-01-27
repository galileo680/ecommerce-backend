package com.bartek.ecommerce.service.Impl;

import com.bartek.ecommerce.dto.ProductDto;
import com.bartek.ecommerce.entity.Category;
import com.bartek.ecommerce.entity.Product;
import com.bartek.ecommerce.exception.NotFoundException;
import com.bartek.ecommerce.mapper.ProductMapper;
import com.bartek.ecommerce.repository.CategoryRepository;
import com.bartek.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private AwsS3Service awsS3Service;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductDto productDto;
    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        productDto = new ProductDto();
        productDto.setName("TestProduct");
        productDto.setDescription("Test Description");
        productDto.setPrice(BigDecimal.TEN);
        productDto.setQuantity(5);
        productDto.setCategoryId(99L);

        product = new Product();
        product.setId(1L);
        product.setName("TestProduct");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.TEN);
        product.setQuantity(5);

        category = new Category();
        category.setId(99L);
        category.setName("TestCategory");
    }

    @Test
    void createProduct_Success() {
        when(productRepository.findByName("TestProduct")).thenReturn(Optional.empty());
        when(categoryRepository.findById(99L)).thenReturn(Optional.of(category));

        when(awsS3Service.saveImageToS3(productDto.getImageFile())).thenReturn("https://s3.url/test.jpg");

        ProductDto mappedDto = new ProductDto();
        mappedDto.setName("TestProduct");
        mappedDto.setImageUrl("https://s3.url/test.jpg");

        when(productMapper.toProductDto(any(Product.class))).thenReturn(mappedDto);

        ProductDto result = productService.createProduct(productDto);

        assertNotNull(result);
        assertEquals("TestProduct", result.getName());
        assertEquals("https://s3.url/test.jpg", result.getImageUrl());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_ThrowsException_WhenNameExists() {
        when(productRepository.findByName("TestProduct"))
                .thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createProduct_ThrowsNotFound_WhenCategoryDoesNotExist() {
        when(productRepository.findByName("TestProduct")).thenReturn(Optional.empty());
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.createProduct(productDto));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toProductDto(any(Product.class))).thenReturn(productDto);

        ProductDto result = productService.updateProduct(1L, productDto);

        assertNotNull(result);
        assertEquals(productDto.getName(), result.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_ThrowsNotFound_WhenProductNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.updateProduct(1L, productDto));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_ThrowsNotFound_WhenCategoryIdNotNullButCategoryIsMissing() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.updateProduct(1L, productDto));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        ProductDto mappedDto = new ProductDto();
        mappedDto.setName("TestProduct");
        when(productMapper.toProductDto(product)).thenReturn(mappedDto);

        ProductDto result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("TestProduct", result.getName());
    }

    @Test
    void getProductById_ThrowsNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void getAllProducts_Success() {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product2");

        List<Product> productList = List.of(product, product2);
        when(productRepository.findAll(any(Sort.class))).thenReturn(productList);

        ProductDto productDto2 = new ProductDto();
        productDto2.setName("Product2");

        when(productMapper.toProductDto(product)).thenReturn(productDto);
        when(productMapper.toProductDto(product2)).thenReturn(productDto2);

        List<ProductDto> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("TestProduct", result.get(0).getName());
        assertEquals("Product2", result.get(1).getName());
    }

    @Test
    void getProductsByCategory_Success() {
        List<Product> products = List.of(product);
        when(productRepository.findByCategoryId(99L)).thenReturn(products);
        when(productMapper.toProductDto(any(Product.class))).thenReturn(productDto);

        List<ProductDto> results = productService.getProductsByCategory(99L);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void getProductsByCategory_ThrowsNotFound() {
        when(productRepository.findByCategoryId(anyLong())).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> productService.getProductsByCategory(1L));
    }

    @Test
    void searchProduct_Success() {
        List<Product> products = List.of(product);
        when(productRepository.findByNameContainingOrDescriptionContaining(anyString(), anyString()))
                .thenReturn(products);
        when(productMapper.toProductDto(any(Product.class))).thenReturn(productDto);

        List<ProductDto> results = productService.searchProduct("Test");

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void searchProduct_NotFound() {
        when(productRepository.findByNameContainingOrDescriptionContaining(anyString(), anyString()))
                .thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> productService.searchProduct("NotFound"));
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void deleteProduct_ThrowsNotFound_WhenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, never()).delete(any(Product.class));
    }
}