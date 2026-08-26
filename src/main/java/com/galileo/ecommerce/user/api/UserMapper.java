package com.galileo.ecommerce.user.api;

import com.galileo.ecommerce.user.api.dto.AddressRequest;
import com.galileo.ecommerce.user.api.dto.AddressResponse;
import com.galileo.ecommerce.user.api.dto.ProfileResponse;
import com.galileo.ecommerce.user.application.AddressData;
import com.galileo.ecommerce.user.domain.Address;
import com.galileo.ecommerce.user.domain.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    ProfileResponse toProfileResponse(User user);

    AddressData toData(AddressRequest request);

    AddressResponse toResponse(Address address);

    List<AddressResponse> toResponses(List<Address> addresses);
}
