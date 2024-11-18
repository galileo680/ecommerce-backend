package com.bartek.ecommerce.service;


import com.bartek.ecommerce.dto.AddressDto;
import com.bartek.ecommerce.entity.Address;
import com.bartek.ecommerce.entity.User;

public interface AddressService {
    Address createOrGetAddress(AddressDto addressDto, User user);
}
