package com.bartek.ecommerce.mapper;

import com.bartek.ecommerce.dto.AddressDto;
import com.bartek.ecommerce.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public static AddressDto toAddressDto(Address address) {
        AddressDto addressDto = AddressDto.builder()
                .id(address.getId())
                .city(address.getCity())
                .street(address.getStreet())
                .state(address.getState())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();

        return addressDto;
    }

    public static Address toAddressEntity(AddressDto dto) {
        if (dto == null) {
            return null;
        }

        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
        address.setState(dto.getState());

        return address;
    }
}
