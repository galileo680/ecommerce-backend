package com.galileo.ecommerce.user;

import com.galileo.ecommerce.TestcontainersConfiguration;
import com.galileo.ecommerce.user.infrastructure.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class AuthIntegrationTest {

    private static final String PASSWORD = "Secret123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Test
    void registrationStoresHashedPasswordAndRejectsDuplicates() throws Exception {
        String email = uniqueEmail();

        register(email).andExpect(status().isCreated());

        assertThat(userRepository.findByEmail(email))
            .hasValueSatisfying(user -> assertThat(user.getPasswordHash()).startsWith("$2"));

        register(email)
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.title").value("Business Rule Violation"));
    }

    @Test
    void weakPasswordIsRejectedWithFieldError() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email": "%s", "password": "short", "firstName": "Jan", "lastName": "Kowalski"}"""
                    .formatted(uniqueEmail())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void loginReturnsTokensAndWrongPasswordReturns401() throws Exception {
        String email = uniqueEmail();
        register(email).andExpect(status().isCreated());

        login(email, PASSWORD)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty());

        login(email, "WrongPass1")
            .andExpect(status().isUnauthorized())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.title").value("Authentication Failed"));
    }

    @Test
    void protectedEndpointRequiresValidToken() throws Exception {
        mockMvc.perform(get("/api/v1/profile")).andExpect(status().isUnauthorized());

        String email = uniqueEmail();
        register(email).andExpect(status().isCreated());
        String access = accessToken(email);

        mockMvc.perform(get("/api/v1/profile").header("Authorization", "Bearer " + access))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.role").value("CUSTOMER"));

        mockMvc.perform(get("/api/v1/profile").header("Authorization", "Bearer " + access + "x"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void expiredTokenIsRejected() throws Exception {
        Instant past = Instant.now().minusSeconds(600);
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("ecommerce")
            .subject(UUID.randomUUID().toString())
            .issuedAt(past.minusSeconds(60))
            .expiresAt(past)
            .claim("roles", List.of("CUSTOMER"))
            .build();
        String expired = jwtEncoder
            .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
            .getTokenValue();

        mockMvc.perform(get("/api/v1/profile").header("Authorization", "Bearer " + expired))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshRotatesAndOldTokenStopsWorking() throws Exception {
        String email = uniqueEmail();
        register(email).andExpect(status().isCreated());
        String firstRefresh = refreshToken(email);

        MvcResult rotated = mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"refreshToken": "%s"}""".formatted(firstRefresh)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andReturn();
        String secondRefresh = JsonPath.read(rotated.getResponse().getContentAsString(), "$.refreshToken");
        assertThat(secondRefresh).isNotEqualTo(firstRefresh);

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"refreshToken": "%s"}""".formatted(firstRefresh)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutInvalidatesRefreshToken() throws Exception {
        String email = uniqueEmail();
        register(email).andExpect(status().isCreated());
        String refresh = refreshToken(email);

        mockMvc.perform(post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"refreshToken": "%s"}""".formatted(refresh)))
            .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"refreshToken": "%s"}""".formatted(refresh)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpointsRejectAnonymousAndCustomer() throws Exception {
        mockMvc.perform(post("/api/v1/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "Should fail"}"""))
            .andExpect(status().isUnauthorized());

        String email = uniqueEmail();
        register(email).andExpect(status().isCreated());
        String customerToken = accessToken(email);

        mockMvc.perform(post("/api/v1/admin/categories")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "Should fail"}"""))
            .andExpect(status().isForbidden());
    }

    @Test
    void userCannotTouchAddressesOfAnotherUser() throws Exception {
        String emailA = uniqueEmail();
        String emailB = uniqueEmail();
        register(emailA).andExpect(status().isCreated());
        register(emailB).andExpect(status().isCreated());
        String tokenA = accessToken(emailA);
        String tokenB = accessToken(emailB);

        MvcResult created = mockMvc.perform(post("/api/v1/profile/addresses")
                .header("Authorization", "Bearer " + tokenA)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"label": "Home", "street": "Main 1", "city": "Warsaw", "postalCode": "00-001", "country": "PL"}"""))
            .andExpect(status().isCreated())
            .andReturn();
        String addressId = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/v1/profile/addresses").header("Authorization", "Bearer " + tokenB))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.addresses").isEmpty());

        mockMvc.perform(put("/api/v1/profile/addresses/{id}", addressId)
                .header("Authorization", "Bearer " + tokenB)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"label": "Hacked", "street": "Evil 1", "city": "Nowhere", "postalCode": "66-666", "country": "PL"}"""))
            .andExpect(status().isNotFound());
    }

    @Test
    void sixthFailedLoginWithinAMinuteIsRateLimited() throws Exception {
        String email = uniqueEmail();
        register(email).andExpect(status().isCreated());

        for (int i = 0; i < 5; i++) {
            login(email, "WrongPass1").andExpect(status().isUnauthorized());
        }

        login(email, "WrongPass1")
            .andExpect(status().isTooManyRequests())
            .andExpect(jsonPath("$.title").value("Too Many Requests"));
    }

    private org.springframework.test.web.servlet.ResultActions register(String email) throws Exception {
        return mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email": "%s", "password": "%s", "firstName": "Jan", "lastName": "Kowalski"}"""
                .formatted(email, PASSWORD)));
    }

    private org.springframework.test.web.servlet.ResultActions login(String email, String password) throws Exception {
        return mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email": "%s", "password": "%s"}""".formatted(email, password)));
    }

    private String accessToken(String email) throws Exception {
        MvcResult result = login(email, PASSWORD).andExpect(status().isOk()).andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
    }

    private String refreshToken(String email) throws Exception {
        MvcResult result = login(email, PASSWORD).andExpect(status().isOk()).andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.refreshToken");
    }

    private String uniqueEmail() {
        return "user-%s@example.com".formatted(UUID.randomUUID().toString().substring(0, 8));
    }
}
