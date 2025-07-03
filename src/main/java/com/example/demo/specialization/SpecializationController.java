package com.example.demo.specialization;

import com.example.demo.specialization.model.SpecializationView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specialization")
@RequiredArgsConstructor
public class SpecializationController implements SpecializationControllerApi {

    private final SpecializationService specializationService; // Will be the interface

    @Override
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')") // Example: Only Admin can create
    public ResponseEntity<SpecializationView> createSpecialization(@RequestBody RequestSpecialization requestSpecialization) {
        SpecializationView created = specializationService.createSpecialization(requestSpecialization);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Override
    @GetMapping("/all")
    // No PreAuthorize, assuming public access as per original controller
    public ResponseEntity<List<SpecializationView>> getAllSpecializations() {
        List<SpecializationView> specializations = specializationService.getAllSpecializations();
        return ResponseEntity.ok(specializations);
    }

    @Override
    @GetMapping("/{id}")
    // No PreAuthorize, assuming public access
    public ResponseEntity<SpecializationView> getSpecializationById(@PathVariable Long id) {
        SpecializationView specialization = specializationService.getSpecializationById(id);
        return ResponseEntity.ok(specialization);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Example: Only Admin can delete
    public ResponseEntity<Void> deleteSpecialization(@PathVariable Long id) {
        specializationService.deleteSpecialization(id);
        return ResponseEntity.ok().build(); // Or noContent()
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Example: Only Admin can update
    public ResponseEntity<SpecializationView> updateSpecialization(@PathVariable Long id, @RequestBody RequestSpecialization requestSpecialization) {
        SpecializationView updated = specializationService.updateSpecialization(id, requestSpecialization);
        return ResponseEntity.ok(updated);
    }
}
