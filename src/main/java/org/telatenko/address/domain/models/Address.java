package org.telatenko.address.domain.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "street", "city", "zipCode"})
public class Address {

    private int id;

    private String street;

    private String city;

    private String zipCode;

    private List<User> users;
}
