package com.example.demo.specialization;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/specialization")
@RequiredArgsConstructor
public class SpecializationController {

    private final SpecializationService specializationService;

    @PostMapping()
    private void createSpecialization(@RequestBody RequestSpecialization requestSpecialization) {
        specializationService.create(requestSpecialization);
    }

    @GetMapping("/all")
    private Iterable<SpecializationEntity> getAllSpecializations() {
        return specializationService.getAll();
    }

    @GetMapping("/{id}")
    private SpecializationEntity getSpecialization(@PathVariable Long id) {
        return specializationService.getById(id);
    }

    @DeleteMapping("/{id}")
    private void deleteSpecialization(@PathVariable Long id) {
        specializationService.delete(id);
    }

    @PutMapping("/{id}")
    private void updateSpecialization(@PathVariable Long id, RequestSpecialization requestSpecialization) {
        specializationService.update(id, requestSpecialization);
    }
}
