package com.example.demo.address.model;

import lombok.Data;

@Data
public class AddressResponse {
    private Long id;
    private String addressName;
    private String addressLocationLink;
    private Double latitude;
    private Double longitude;
}

