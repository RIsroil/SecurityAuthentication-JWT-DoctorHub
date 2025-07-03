package com.example.demo.branch;

import com.example.demo.branch.model.BranchRequest;
import com.example.demo.branch.model.BranchUpdateRequest;
import com.example.demo.branch.model.BranchView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Branch", description = "Branch management APIs")
@SecurityRequirement(name = "bearerAuth")
public interface BranchControllerApi {

    @Operation(summary = "Create a new branch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created branch"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<?> createBranch(Principal principal, @RequestBody BranchRequest request);

    @Operation(summary = "Get my branches")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved branches"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<List<BranchView>> getMyBranches(Principal principal);

    @Operation(summary = "Get branch by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved branch"),
            @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    ResponseEntity<BranchView> getBranchById(@PathVariable Long id);

    @Operation(summary = "Update an existing branch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated branch"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    ResponseEntity<?> updateBranch(Principal principal, @PathVariable Long id, @RequestBody BranchUpdateRequest request);

    @Operation(summary = "Delete a branch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted branch"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    ResponseEntity<?> deleteBranch(Principal principal, @PathVariable Long id);
}
