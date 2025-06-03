package com.example.demo.address;

import com.example.demo.address.model.AddressRequest;
import com.example.demo.address.model.AddressResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(@RequestBody AddressRequest addressRequest) {
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
