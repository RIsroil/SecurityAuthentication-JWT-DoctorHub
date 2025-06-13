package com.example.demo.branch;

import com.example.demo.branch.model.BranchRequest;
import com.example.demo.branch.model.BranchResponse;
import com.example.demo.branch.model.BranchUpdateRequest;
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
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> create(Principal principal,@Valid @RequestBody BranchRequest branchRequest) {
        return branchService.createBranch(principal, branchRequest);
    }

    @GetMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public List<BranchResponse> getMyBranches(Principal principal) {
        return branchService.getMyBranches(principal);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBranch(Principal principal, @PathVariable Long id) {
        return ResponseEntity.ok(branchService.deleteBranch(principal, id));
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateBranch(Principal principal, @PathVariable Long id, @RequestBody BranchUpdateRequest branchRequest){
        return ResponseEntity.ok(branchService.updateBranch(principal, id, branchRequest));
    }

    @GetMapping("/{id}")
    public BranchResponse  getBranch(@PathVariable Long id) {
        return branchService.getBranchById(id);
    }
}
