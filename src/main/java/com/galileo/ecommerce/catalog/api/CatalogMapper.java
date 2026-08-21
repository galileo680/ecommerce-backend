package com.galileo.ecommerce.catalog.api;

import com.galileo.ecommerce.catalog.api.CategoryDtos.CategoryResponse;
import com.galileo.ecommerce.catalog.api.ProductDtos.CreateProductRequest;
import com.galileo.ecommerce.catalog.api.ProductDtos.ProductResponse;
import com.galileo.ecommerce.catalog.api.ProductDtos.UpdateProductRequest;
import com.galileo.ecommerce.catalog.application.CategoryNode;
import com.galileo.ecommerce.catalog.application.ProductCommands.CreateProduct;
import com.galileo.ecommerce.catalog.application.ProductCommands.UpdateProduct;
import com.galileo.ecommerce.catalog.domain.Product;
import com.galileo.ecommerce.catalog.domain.Sku;
import com.galileo.ecommerce.common.domain.Money;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CatalogMapper {

    CreateProduct toCommand(CreateProductRequest request);

    UpdateProduct toCommand(UpdateProductRequest request);

    ProductResponse toResponse(Product product);

    CategoryResponse toResponse(CategoryNode node);

    List<CategoryResponse> toCategoryResponses(List<CategoryNode> nodes);

    default Money toMoney(MoneyDto dto) {
        return dto == null ? null : Money.of(dto.amount(), dto.currency());
    }

    default MoneyDto toDto(Money money) {
        return money == null ? null : new MoneyDto(money.amount(), money.currency());
    }

    default String toValue(Sku sku) {
        return sku == null ? null : sku.value();
    }
}
