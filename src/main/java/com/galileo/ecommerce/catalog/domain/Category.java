package com.galileo.ecommerce.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "category", schema = "catalog")
public class Category {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "parent_id")
    private UUID parentId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Category() {
    }

    private Category(UUID id, String name, UUID parentId) {
        this.id = id;
        this.name = name;
        this.slug = slugOf(name);
        this.parentId = parentId;
    }

    public static Category create(String name, UUID parentId) {
        return new Category(UUID.randomUUID(), name, parentId);
    }

    public void rename(String name) {
        this.name = name;
        this.slug = slugOf(name);
    }

    public static String slugOf(String name) {
        return name.trim().toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("(^-|-$)", "");
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public UUID getParentId() {
        return parentId;
    }
}
