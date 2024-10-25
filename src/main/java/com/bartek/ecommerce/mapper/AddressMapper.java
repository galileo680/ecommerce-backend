package com.bartek.ecommerce.mapper;

import com.bartek.ecommerce.dto.AddressDto;
import com.bartek.ecommerce.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressDto toAddressDto(Address address) {
        AddressDto addressDto = AddressDto.builder()
                .id(address.getId())
                .city(address.getCity())
                .street(address.getStreet())
                .state(address.getState())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .build();

        return addressDto;
    }
}
