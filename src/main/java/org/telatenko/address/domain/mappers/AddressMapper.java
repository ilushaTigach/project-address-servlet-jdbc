package org.telatenko.address.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.telatenko.address.domain.dtos.AddressDto;
import org.telatenko.address.domain.models.Address;
import java.util.List;

@Mapper
public interface AddressMapper {

    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    List<AddressDto> toDtos(List<Address> addresses);

    AddressDto toDto(Address address);

    Address toEntity(AddressDto addressDto);
}
