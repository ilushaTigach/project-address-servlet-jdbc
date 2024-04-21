package org.telatenko.address.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.telatenko.address.domain.dtos.UserDto;
import org.telatenko.address.domain.models.User;
import java.util.List;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    List<UserDto> toDto(List<User> users);

    @Mapping(target = "addressId", source = "address.id")
    @Mapping(target = "phoneId", source = "phone.id")
    UserDto toDto(User user);

    @Mapping(target = "address.id", source = "addressId")
    @Mapping(target = "phone.id", source = "phoneId")
    User toEntity(UserDto userDto);
}
