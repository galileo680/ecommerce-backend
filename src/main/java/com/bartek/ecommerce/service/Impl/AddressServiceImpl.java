package com.bartek.ecommerce.service.Impl;

import com.bartek.ecommerce.dto.AddressDto;
import com.bartek.ecommerce.entity.Address;
import com.bartek.ecommerce.entity.User;
import com.bartek.ecommerce.repository.AddressRepository;
import com.bartek.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    public Address createOrGetAddress(AddressDto addressDto, User user) {
        Address address = new Address();
        address.setUser(user);
        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        address.setPostalCode(addressDto.getPostalCode());
        address.setCountry(addressDto.getCountry());
        address.setState(addressDto.getState());

        return addressRepository.save(address);
    }
}
