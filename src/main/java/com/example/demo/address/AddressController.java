package com.example.demo.address;

import com.example.demo.address.model.AddressRequest;
import com.example.demo.address.model.AddressView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<?> createAddress(@RequestBody AddressRequest addressRequest) {
        return addressService.create(addressRequest);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable Long id, @RequestBody AddressRequest addressRequest) {
        return addressService.update(id, addressRequest);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    // Addressni o'chirish
    @DeleteMapping("/{id}")
    public void deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
    }

    @GetMapping("/id")
    public AddressEntity getAddressById(@RequestParam Long id) {
        return addressService.getById(id);
    }
}
