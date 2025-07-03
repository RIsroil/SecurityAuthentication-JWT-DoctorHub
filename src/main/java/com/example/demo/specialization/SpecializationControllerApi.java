package com.example.demo.specialization;

import com.example.demo.specialization.model.SpecializationView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Specialization", description = "Specialization management APIs")
public interface SpecializationControllerApi {

    @Operation(summary = "Create a new specialization (Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Specialization created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., name already exists)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only Admins can create")
    })
    @PostMapping()
    @SecurityRequirement(name = "bearerAuth") // Assuming admin only
    ResponseEntity<SpecializationView> createSpecialization(@RequestBody RequestSpecialization requestSpecialization);

    @Operation(summary = "Get all specializations (Public)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all specializations")
    })
    @GetMapping("/all")
    ResponseEntity<List<SpecializationView>> getAllSpecializations();

    @Operation(summary = "Get a specific specialization by ID (Public)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved specialization"),
            @ApiResponse(responseCode = "404", description = "Specialization not found")
    })
    @GetMapping("/{id}")
    ResponseEntity<SpecializationView> getSpecializationById(@PathVariable Long id);

    @Operation(summary = "Delete a specialization (Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Specialization deleted successfully (No Content)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only Admins can delete"),
            @ApiResponse(responseCode = "404", description = "Specialization not found")
    })
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth") // Assuming admin only
    ResponseEntity<Void> deleteSpecialization(@PathVariable Long id);

    @Operation(summary = "Update an existing specialization (Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Specialization updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only Admins can update"),
            @ApiResponse(responseCode = "404", description = "Specialization not found")
    })
    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth") // Assuming admin only
    ResponseEntity<SpecializationView> updateSpecialization(@PathVariable Long id, @RequestBody RequestSpecialization requestSpecialization);
}
