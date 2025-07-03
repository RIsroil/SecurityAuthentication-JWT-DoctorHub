package com.example.demo.disease;

import com.example.demo.disease.model.DiseaseRequest;
import com.example.demo.disease.model.DiseaseResponse; // Controller returns DiseaseResponse
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Disease", description = "Disease (Service/Price) management APIs for Doctors")
@SecurityRequirement(name = "bearerAuth")
public interface DiseaseControllerApi {

    @Operation(summary = "Create a new disease/service for a specific branch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Disease created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or branch ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Doctor does not own this branch"),
            @ApiResponse(responseCode = "404", description = "Branch or Doctor not found")
    })
    @PostMapping() // Assuming branchId is a request param as in original controller
    ResponseEntity<DiseaseResponse> createDisease(Principal principal, @RequestParam Long branchId, @RequestBody DiseaseRequest request);

    @Operation(summary = "Get all diseases/services for the currently authenticated doctor (across all their branches)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved diseases"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @GetMapping()
    ResponseEntity<List<DiseaseResponse>> getAllDiseasesByDoctor(Principal principal);

    @Operation(summary = "Get a specific disease/service by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved disease"),
            // @ApiResponse(responseCode = "401", description = "Unauthorized"), // If public, no 401
            @ApiResponse(responseCode = "404", description = "Disease not found")
    })
    @GetMapping("/{id}")
    ResponseEntity<DiseaseResponse> getDiseaseById(@PathVariable Long id);

    @Operation(summary = "Update an existing disease/service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disease updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or disease ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Doctor does not own this disease"),
            @ApiResponse(responseCode = "404", description = "Disease or Doctor not found")
    })
    @PatchMapping("/{id}")
    ResponseEntity<DiseaseResponse> updateDisease(Principal principal, @PathVariable Long id, @RequestBody DiseaseRequest request);

    @Operation(summary = "Delete a disease/service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disease deleted successfully (No Content)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Doctor does not own this disease"),
            @ApiResponse(responseCode = "404", description = "Disease or Doctor not found")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteDisease(Principal principal, @PathVariable Long id);
}
