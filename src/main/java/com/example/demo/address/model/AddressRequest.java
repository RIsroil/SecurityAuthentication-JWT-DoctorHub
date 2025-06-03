package com.example.demo.address.model;

import lombok.Data;

@Data
public class AddressRequest {
    private String addressName;
    private String addressLocationLink; // ixtiyoriy, agar nom orqali topilmasa, admin link kiritadi

}
