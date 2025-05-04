package com.bartek.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto implements Serializable {

    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 15,  message = "Wrong name for a product")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 50,  message = "Wrong description for a product")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "1.0", inclusive = false, message = "Price must be greater than 1.0")
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10000, message = "Quantity cannot exceed 10000")
    private Integer quantity;

    private String imageUrl;

    @NotNull(message = "Product image is required")
    private MultipartFile imageFile;

    private byte[] retrievedImage;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private boolean archived;

    private LocalDateTime archivedAt;
}