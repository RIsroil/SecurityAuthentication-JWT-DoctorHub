package com.example.demo.branch;

import com.example.demo.branch.model.BranchRequest;
import com.example.demo.branch.model.BranchUpdateRequest;
import com.example.demo.branch.model.BranchView;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/branch")
@RequiredArgsConstructor
public class BranchController implements BranchControllerApi {

    private final BranchService branchService;

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @Override
    public ResponseEntity<?> createBranch(Principal principal, @Valid @RequestBody BranchRequest branchRequest) {
        return branchService.createBranch(principal, branchRequest);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @Override
    public ResponseEntity<List<BranchView>> getMyBranches(Principal principal) {
        return ResponseEntity.ok(branchService.getMyBranches(principal));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @Override
    public ResponseEntity<?> deleteBranch(Principal principal, @PathVariable Long id) {
        return branchService.deleteBranch(principal, id);
    }

    @PatchMapping("/{id}") // Changed from PutMapping to PatchMapping to match original
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @Override
    public ResponseEntity<?> updateBranch(Principal principal, @PathVariable Long id, @RequestBody BranchUpdateRequest branchRequest){
        return branchService.updateBranch(principal, id, branchRequest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @Override
    public ResponseEntity<BranchView> getBranchById(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.getBranchById(id));
    }
}
