package org.telatenko.address.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.telatenko.address.domain.dtos.PhoneDto;
import org.telatenko.address.domain.models.Phone;
import java.util.List;

@Mapper
public interface PhoneMapper {

    PhoneMapper INSTANCE = Mappers.getMapper(PhoneMapper.class);

    List<PhoneDto> toDtos(List<Phone> phones);

    PhoneDto toDto(Phone  phone);

    Phone toEntity(PhoneDto  phoneDto);
}
