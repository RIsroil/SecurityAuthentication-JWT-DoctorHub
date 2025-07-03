package com.example.demo.address;

import com.example.demo.address.model.AddressRequest;
import com.example.demo.address.model.AddressView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Address", description = "Address management APIs")
public interface AddressControllerApi {

    @Operation(summary = "Create a new address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created address"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    ResponseEntity<?> createAddress(@RequestBody AddressRequest request);

    @Operation(summary = "Get all addresses")
    ResponseEntity<List<AddressView>> getAllAddresses();

    @Operation(summary = "Get address by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved address"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    ResponseEntity<AddressView> getAddressById(@PathVariable Long id);

    @Operation(summary = "Update an existing address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated address"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    ResponseEntity<AddressView> updateAddress(@PathVariable Long id, @RequestBody AddressRequest request);

    @Operation(summary = "Delete an address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted address"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    ResponseEntity<Void> deleteAddress(@PathVariable Long id);
}
