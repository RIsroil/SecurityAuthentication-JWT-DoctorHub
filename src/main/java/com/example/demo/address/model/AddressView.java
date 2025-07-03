package com.example.demo.address.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressView {
    private Long id;
    private String addressName;
    private String addressLocationLink;
    private double latitude;
    private double longitude;
}
