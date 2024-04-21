package org.telatenko.address.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private int id;

    private String name;

    private String email;

    private int addressId;

    private int phoneId;
}
