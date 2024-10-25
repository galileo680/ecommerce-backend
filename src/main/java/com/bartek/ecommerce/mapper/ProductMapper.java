package com.bartek.ecommerce.mapper;


import com.bartek.ecommerce.dto.ProductDto;
import com.bartek.ecommerce.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDto toProductDto(Product product) {
        ProductDto productDto = ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .build();

        return productDto;
    }
}
