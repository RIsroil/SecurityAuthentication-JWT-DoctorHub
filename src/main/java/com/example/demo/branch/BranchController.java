package com.example.demo.branch;

import com.example.demo.branch.model.BranchRequest;
import com.example.demo.branch.model.BranchResponse;
import com.example.demo.branch.model.BranchUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/branch")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping()
    public ResponseEntity<?> create(String accessToken, @RequestBody BranchRequest branchRequest) {
        return ResponseEntity.ok(branchService.createBranch(accessToken, branchRequest));
    }

    @GetMapping()
    public List<BranchResponse> getMyBranches(String accessToken) {
        return branchService.getMyBranches(accessToken);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBranch(@RequestParam String accessToken, @PathVariable Long id) {
        return ResponseEntity.ok(branchService.deleteBranch(accessToken, id));
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateBranch(@RequestParam String accessToken, @PathVariable Long id, @RequestBody BranchUpdateRequest branchRequest){
        return ResponseEntity.ok(branchService.updateBranch(accessToken, id, branchRequest));
    }

    @GetMapping("/{id}")
    public BranchResponse  getBranch(@PathVariable Long id) {
        return branchService.getBranchById(id);
    }
}
