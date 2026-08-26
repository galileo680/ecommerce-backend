package com.galileo.ecommerce.user.application;

import com.galileo.ecommerce.common.domain.ResourceNotFoundException;
import com.galileo.ecommerce.user.domain.Address;
import com.galileo.ecommerce.user.domain.User;
import com.galileo.ecommerce.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository users;

    @Transactional(readOnly = true)
    public User getProfile(UUID userId) {
        return requireUser(userId);
    }

    @Transactional(readOnly = true)
    public List<Address> getAddresses(UUID userId) {
        return requireUser(userId).getAddresses();
    }

    public void updateProfile(UUID userId, String firstName, String lastName) {
        requireUser(userId).updateProfile(firstName, lastName);
    }

    public UUID addAddress(UUID userId, AddressData data) {
        return requireUser(userId)
            .addAddress(data.label(), data.street(), data.city(), data.postalCode(), data.country());
    }

    public void updateAddress(UUID userId, UUID addressId, AddressData data) {
        requireUser(userId)
            .updateAddress(addressId, data.label(), data.street(), data.city(), data.postalCode(), data.country());
    }

    public void removeAddress(UUID userId, UUID addressId) {
        requireUser(userId).removeAddress(addressId);
    }

    public void markDefaultAddress(UUID userId, UUID addressId) {
        requireUser(userId).markDefaultAddress(addressId);
    }

    private User requireUser(UUID userId) {
        return users.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("account %s not found".formatted(userId)));
    }
}
