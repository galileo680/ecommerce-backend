package com.bartek.ecommerce.repository;

import com.bartek.ecommerce.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}