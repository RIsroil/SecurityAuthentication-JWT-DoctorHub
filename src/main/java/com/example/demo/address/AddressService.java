package com.example.demo.address;

import com.example.demo.address.model.AddressRequest;
import com.example.demo.address.model.AddressView;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AddressService {
    ResponseEntity<?> create(AddressRequest request);
    List<AddressView> getAllAddresses();
    ResponseEntity<AddressView> update(Long id, AddressRequest request);
    void deleteAddress(Long id);
    AddressView getById(Long id);
}
