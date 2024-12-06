package com.bartek.ecommerce.controller;


import com.bartek.ecommerce.dto.ProductDto;
import com.bartek.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductById(
            @PathVariable Long productId
    ){
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    //Get by category id
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(
            @PathVariable Long categoryId
    ){
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchForProduct(
            @RequestParam String searchValue
    ) {
        return ResponseEntity.ok(productService.searchProduct(searchValue));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductDto> createProduct(
            @ModelAttribute @Valid ProductDto productDto
    ){
        ProductDto productDtoCreated = productService.createProduct(productDto);
        return new ResponseEntity<>(productDtoCreated, HttpStatus.CREATED);
    }

    @PutMapping("{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(
            @ModelAttribute @Valid ProductDto productDto,
            @PathVariable Long productId
    ){
        return ResponseEntity.ok(productService.updateProduct(productId, productDto));
    }

    @DeleteMapping("{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<HttpStatus> deleteProduct(
            @PathVariable Long productId
    ) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
