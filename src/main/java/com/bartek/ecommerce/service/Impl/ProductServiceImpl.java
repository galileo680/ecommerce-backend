package com.bartek.ecommerce.service.Impl;

import com.bartek.ecommerce.dto.ProductDto;
import com.bartek.ecommerce.entity.Category;
import com.bartek.ecommerce.entity.Product;
import com.bartek.ecommerce.exception.NotFoundException;
import com.bartek.ecommerce.mapper.ProductMapper;
import com.bartek.ecommerce.repository.CategoryRepository;
import com.bartek.ecommerce.repository.ProductRepository;
import com.bartek.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final AwsS3Service awsS3Service;

    @Override
    @CacheEvict(value = "productCache", allEntries = true)
    public ProductDto createProduct(ProductDto productDto) {
        if(productRepository.findByName(productDto.getName()).isPresent()){
            throw new IllegalArgumentException("Provided product name is already in use");
        }

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(()-> new NotFoundException("Category not found"));

        String productImageUrl = awsS3Service.saveImageToS3(productDto.getImageFile());

        Product product = new Product();
        product.setCategory(category);
        product.setPrice(productDto.getPrice());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setImageUrl(productImageUrl);
        product.setImageFileName(productDto.getImageFile().getOriginalFilename());
        product.setQuantity(productDto.getQuantity());

        productRepository.save(product);

        ProductDto productDtoMapped = productMapper.toProductDto(product);

        return productDtoMapped;
    }

    @Override
    @CachePut(value = "productCache", key = "#productId")
    public ProductDto updateProduct(Long productId, ProductDto productDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new NotFoundException("Product Not Found"));

        Category category = null;
        String productImageUrl = null;

        if(productDto.getCategoryId() != null ){
            category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(()-> new NotFoundException("Category not found"));

            product.setCategory(category);
        }
        if (productDto.getImageFile() != null && !productDto.getImageFile().isEmpty()){
            productImageUrl = awsS3Service.saveImageToS3(productDto.getImageFile());

            product.setImageUrl(productImageUrl);
        }

        Optional.ofNullable(productDto.getName()).ifPresent(product::setName);
        Optional.ofNullable(productDto.getDescription()).ifPresent(product::setDescription);
        Optional.ofNullable(productDto.getPrice()).ifPresent(product::setPrice);
        Optional.ofNullable(productDto.getQuantity()).ifPresent(product::setQuantity);


        //TODO: mapping saved product
        productRepository.save(product);

        ProductDto productDtoMapped = productMapper.toProductDto(product);

        return productDtoMapped;
    }

    @Override
    @Cacheable(value = "productCache", key = "#productId")
    public ProductDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new NotFoundException("Product Not Found"));

        ProductDto productDto = productMapper.toProductDto(product);

        return productDto;
    }

    @Override
    @Cacheable(value = "productCache", key = "'allProducts'")
    public List<ProductDto> getAllProducts() {
        List<ProductDto> productList = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(productMapper::toProductDto)
                .collect(Collectors.toList());

        return productList;
    }

    @Override
    @Cacheable(value = "productCache", key = "'category_' + #categoryId")
    public List<ProductDto> getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);

        if(products.isEmpty()){
            throw new NotFoundException("No Products found for this category");
        }

        List<ProductDto> productDtoList = products.stream()
                .map(productMapper::toProductDto)
                .collect(Collectors.toList());

        return productDtoList;
    }

    @Override
    public List<ProductDto> searchProduct(String searchValue) {
        List<Product> products = productRepository.findByNameContainingOrDescriptionContaining(searchValue, searchValue);

        if (products.isEmpty()){
            throw new NotFoundException("No Products Found");
        }

        List<ProductDto> productDtoList = products.stream()
                .map(productMapper::toProductDto)
                .collect(Collectors.toList());

        return productDtoList;
    }

    @Override
    @CacheEvict(value = "productCache", key = "#productId")
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new NotFoundException("Product Not Found"));

        productRepository.delete(product);
    }
}
