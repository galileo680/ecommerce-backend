package com.galileo.ecommerce.catalog;

import com.galileo.ecommerce.TestcontainersConfiguration;
import com.galileo.ecommerce.catalog.domain.ProductStatus;
import com.galileo.ecommerce.catalog.infrastructure.ProductRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class CatalogApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void productLifecycleFromDraftToArchive() throws Exception {
        String categoryId = createCategory("Lifecycle %s".formatted(UUID.randomUUID()), null);
        String productId = idFrom(createProduct("LIF-%s".formatted(unique()), "Lifecycle product", "4999.00", categoryId));

        mockMvc.perform(get("/api/v1/products/{id}", productId))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

        mockMvc.perform(post("/api/v1/admin/products/{id}/activate", productId))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/products/{id}", productId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Lifecycle product"))
            .andExpect(jsonPath("$.price.amount").value(4999.00))
            .andExpect(jsonPath("$.price.currency").value("PLN"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(put("/api/v1/admin/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "Renamed product", "price": {"amount": 5100.00, "currency": "PLN"},
                     "categoryId": "%s", "attributes": {}}""".formatted(categoryId)))
            .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/v1/admin/products/{id}", productId))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/products/{id}", productId))
            .andExpect(status().isNotFound());

        assertThat(productRepository.findById(UUID.fromString(productId)))
            .hasValueSatisfying(product -> {
                assertThat(product.getStatus()).isEqualTo(ProductStatus.ARCHIVED);
                assertThat(product.getName()).isEqualTo("Renamed product");
            });
    }

    @Test
    void invalidProductReturnsProblemJsonWithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/v1/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"sku": "x", "name": "", "price": {"amount": -5, "currency": "zl"}}"""))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.errors.sku").exists())
            .andExpect(jsonPath("$.errors.name").exists())
            .andExpect(jsonPath("$.errors.categoryId").exists())
            .andExpect(jsonPath("$.errors").value(Matchers.hasKey("price.amount")));
    }

    @Test
    void duplicateSkuReturnsConflict() throws Exception {
        String categoryId = createCategory("Duplicates %s".formatted(UUID.randomUUID()), null);
        String sku = "DUP-%s".formatted(unique());
        createProduct(sku, "First", "10.00", categoryId);

        mockMvc.perform(post("/api/v1/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson(sku, "Second", "10.00", categoryId)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.title").value("Business Rule Violation"));
    }

    @Test
    void filtersSortingAndPagingWorkTogether() throws Exception {
        String categoryId = createCategory("Filters %s".formatted(UUID.randomUUID()), null);
        activate(createProduct("FIL-%s".formatted(unique()), "Alpha Widget", "100.00", categoryId));
        activate(createProduct("FIL-%s".formatted(unique()), "Beta Widget", "200.00", categoryId));
        activate(createProduct("FIL-%s".formatted(unique()), "Gamma Gadget", "300.00", categoryId));

        mockMvc.perform(get("/api/v1/products")
                .param("category", categoryId)
                .param("minPrice", "150").param("maxPrice", "350").param("search", "widget"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Beta Widget"));

        mockMvc.perform(get("/api/v1/products")
                .param("category", categoryId).param("sort", "price,asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Alpha Widget"))
            .andExpect(jsonPath("$.content[2].name").value("Gamma Gadget"));

        mockMvc.perform(get("/api/v1/products").param("size", "500"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size").value(50));

        mockMvc.perform(get("/api/v1/products").param("sort", "hack,asc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void categoryTreeAndDeletionRules() throws Exception {
        String parentName = "Parent %s".formatted(UUID.randomUUID());
        String parentId = createCategory(parentName, null);
        String childId = createCategory("Child %s".formatted(UUID.randomUUID()), parentId);
        createProduct("CAT-%s".formatted(unique()), "Category product", "10.00", childId);

        MvcResult tree = mockMvc.perform(get("/api/v1/categories"))
            .andExpect(status().isOk())
            .andReturn();
        assertThat(tree.getResponse().getContentAsString()).contains(parentName);

        mockMvc.perform(delete("/api/v1/admin/categories/{id}", parentId))
            .andExpect(status().isConflict());
        mockMvc.perform(delete("/api/v1/admin/categories/{id}", childId))
            .andExpect(status().isConflict());
    }

    private String createCategory(String name, String parentId) throws Exception {
        String parent = parentId == null ? "null" : "\"%s\"".formatted(parentId);
        MvcResult result = mockMvc.perform(post("/api/v1/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "%s", "parentId": %s}""".formatted(name, parent)))
            .andExpect(status().isCreated())
            .andReturn();
        return com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$.id");
    }

    private String createProduct(String sku, String name, String amount, String categoryId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson(sku, name, amount, categoryId)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andReturn();
        return result.getResponse().getHeader("Location");
    }

    private String productJson(String sku, String name, String amount, String categoryId) {
        return """
            {"sku": "%s", "name": "%s", "description": "test product",
             "price": {"amount": %s, "currency": "PLN"},
             "categoryId": "%s", "attributes": {"origin": "test"}}""".formatted(sku, name, amount, categoryId);
    }

    private void activate(String location) throws Exception {
        mockMvc.perform(post("/api/v1/admin/products/{id}/activate", idFrom(location)))
            .andExpect(status().isNoContent());
    }

    private String idFrom(String location) {
        return location.substring(location.lastIndexOf("/") + 1);
    }

    private String unique() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
