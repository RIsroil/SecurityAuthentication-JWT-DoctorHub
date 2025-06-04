package com.example.demo.address.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AddressRequest {
    private String addressName;
    @Schema(example = "If a link is entered, the coordinates will be automatically determined from the link, otherwise it will try to find it from the name.")
    private String addressLocationLink;

}
