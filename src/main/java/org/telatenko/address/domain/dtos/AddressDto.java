package org.telatenko.address.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private int id;

    private String street;

    private String city;

    private String zipCode;

    private List<UserDto> users;
}
