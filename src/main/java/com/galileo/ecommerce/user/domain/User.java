package com.galileo.ecommerce.user.domain;

import com.galileo.ecommerce.common.domain.BusinessRuleException;
import com.galileo.ecommerce.common.domain.ResourceNotFoundException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "account", schema = "users")
public class User {

    private static final int MAX_ADDRESSES = 10;

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_id", nullable = false)
    private List<Address> addresses = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected User() {
    }

    private User(UUID id, String email, String passwordHash, String firstName, String lastName, UserRole role) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public static User register(String email, String passwordHash, String firstName, String lastName) {
        return new User(UUID.randomUUID(), normalizeEmail(email), passwordHash, firstName, lastName,
            UserRole.CUSTOMER);
    }

    public static User registerAdmin(String email, String passwordHash, String firstName, String lastName) {
        return new User(UUID.randomUUID(), normalizeEmail(email), passwordHash, firstName, lastName, UserRole.ADMIN);
    }

    public static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public void updateProfile(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UUID addAddress(String label, String street, String city, String postalCode, String country) {
        if (addresses.size() >= MAX_ADDRESSES) {
            throw new BusinessRuleException("an account cannot have more than %d addresses".formatted(MAX_ADDRESSES));
        }
        Address address = new Address(label, street, city, postalCode, country, addresses.isEmpty());
        addresses.add(address);
        return address.getId();
    }

    public void updateAddress(UUID addressId, String label, String street, String city, String postalCode,
                              String country) {
        requireAddress(addressId).update(label, street, city, postalCode, country);
    }

    public void removeAddress(UUID addressId) {
        Address address = requireAddress(addressId);
        addresses.remove(address);
        if (address.isDefaultAddress() && !addresses.isEmpty()) {
            addresses.getFirst().markDefault(true);
        }
    }

    public void markDefaultAddress(UUID addressId) {
        Address target = requireAddress(addressId);
        addresses.forEach(address -> address.markDefault(false));
        target.markDefault(true);
    }

    private Address requireAddress(UUID addressId) {
        return addresses.stream()
            .filter(address -> address.getId().equals(addressId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("address %s not found".formatted(addressId)));
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UserRole getRole() {
        return role;
    }

    public List<Address> getAddresses() {
        return List.copyOf(addresses);
    }
}
