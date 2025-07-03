package com.example.demo.branch;

import com.example.demo.branch.model.BranchRequest;
import com.example.demo.branch.model.BranchUpdateRequest;
import com.example.demo.branch.model.BranchView;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface BranchService {
    ResponseEntity<?> createBranch(Principal principal, BranchRequest request);
    List<BranchView> getMyBranches(Principal principal);
    ResponseEntity<?> deleteBranch(Principal principal, Long id);
    ResponseEntity<?> updateBranch(Principal principal, Long id, BranchUpdateRequest request);
    BranchView getBranchById(Long id);
}
