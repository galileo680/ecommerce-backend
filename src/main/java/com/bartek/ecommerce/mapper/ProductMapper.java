package com.bartek.ecommerce.mapper;


import com.bartek.ecommerce.dto.ProductDto;
import com.bartek.ecommerce.entity.Product;
import com.bartek.ecommerce.service.Impl.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final AwsS3Service awsS3Service;

    public ProductDto toProductDto(Product product) {
        ProductDto productDto = ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .categoryId(product.getCategory().getId())
                .imageUrl(product.getImageUrl())
                .build();

        return productDto;
    }
}
