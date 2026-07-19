package com.code81.onlinestore.mapper;

import com.code81.onlinestore.dto.address.AddressRequest;
import com.code81.onlinestore.dto.address.AddressResponse;
import com.code81.onlinestore.entity.Address;
import com.code81.onlinestore.entity.Customer;

public final class AddressMapper {

    private AddressMapper() {
    }

    public static Address toEntity(AddressRequest request, Customer customer) {
        return Address.builder()
                .customer(customer)
                .label(request.getLabel())
                .line1(request.getLine1())
                .line2(request.getLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .isDefault(request.isDefault())
                .build();
    }

    public static void updateEntity(Address address, AddressRequest request) {
        address.setLabel(request.getLabel());
        address.setLine1(request.getLine1());
        address.setLine2(request.getLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setDefault(request.isDefault());
    }

    public static AddressResponse toResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .label(address.getLabel())
                .line1(address.getLine1())
                .line2(address.getLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.isDefault())
                .build();
    }
}
