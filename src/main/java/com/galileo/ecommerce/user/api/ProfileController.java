package com.galileo.ecommerce.user.api;

import com.galileo.ecommerce.user.api.dto.AddressListResponse;
import com.galileo.ecommerce.user.api.dto.AddressRequest;
import com.galileo.ecommerce.user.api.dto.IdResponse;
import com.galileo.ecommerce.user.api.dto.ProfileResponse;
import com.galileo.ecommerce.user.api.dto.UpdateProfileRequest;
import com.galileo.ecommerce.user.application.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "Profile", description = "Profile and addresses of the logged in user")
@RequiredArgsConstructor
class ProfileController {

    private final ProfileService profileService;
    private final UserMapper mapper;

    @GetMapping
    @Operation(summary = "Get own profile")
    ProfileResponse profile(@AuthenticationPrincipal Jwt jwt) {
        return mapper.toProfileResponse(profileService.getProfile(userId(jwt)));
    }

    @PutMapping
    @Operation(summary = "Update own profile")
    ResponseEntity<Void> updateProfile(@AuthenticationPrincipal Jwt jwt,
                                       @Valid @RequestBody UpdateProfileRequest request) {
        profileService.updateProfile(userId(jwt), request.firstName(), request.lastName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/addresses")
    @Operation(summary = "List own addresses")
    AddressListResponse addresses(@AuthenticationPrincipal Jwt jwt) {
        return new AddressListResponse(mapper.toResponses(profileService.getAddresses(userId(jwt))));
    }

    @PostMapping("/addresses")
    @Operation(summary = "Add an address, the first one becomes the default")
    ResponseEntity<IdResponse> addAddress(@AuthenticationPrincipal Jwt jwt,
                                          @Valid @RequestBody AddressRequest request) {
        UUID id = profileService.addAddress(userId(jwt), mapper.toData(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(id));
    }

    @PutMapping("/addresses/{addressId}")
    @Operation(summary = "Update an own address")
    ResponseEntity<Void> updateAddress(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID addressId,
                                       @Valid @RequestBody AddressRequest request) {
        profileService.updateAddress(userId(jwt), addressId, mapper.toData(request));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/addresses/{addressId}")
    @Operation(summary = "Remove an own address")
    ResponseEntity<Void> removeAddress(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID addressId) {
        profileService.removeAddress(userId(jwt), addressId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/addresses/{addressId}/default")
    @Operation(summary = "Mark an own address as the default one")
    ResponseEntity<Void> markDefault(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID addressId) {
        profileService.markDefaultAddress(userId(jwt), addressId);
        return ResponseEntity.noContent().build();
    }

    private UUID userId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
