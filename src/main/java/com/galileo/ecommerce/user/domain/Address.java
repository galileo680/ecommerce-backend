package com.galileo.ecommerce.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "address", schema = "users")
public class Address {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String country;

    @Column(name = "is_default", nullable = false)
    private boolean defaultAddress;

    protected Address() {
    }

    Address(String label, String street, String city, String postalCode, String country, boolean defaultAddress) {
        this.id = UUID.randomUUID();
        this.label = label;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.defaultAddress = defaultAddress;
    }

    void update(String label, String street, String city, String postalCode, String country) {
        this.label = label;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }

    void markDefault(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public UUID getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }
}
