package org.telatenko.address.domain.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "name", "email", "addressId", "phoneId"})
public class User {

    private int id;

    private String name;

    private String email;

    private Address address;

    private Phone phone;
}
