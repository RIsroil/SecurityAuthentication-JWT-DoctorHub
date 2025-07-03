package com.example.demo.disease;

import com.example.demo.disease.mapper.DiseaseMapper;
import com.example.demo.disease.model.DiseaseRequest;
import com.example.demo.disease.model.DiseaseResponse;
import com.example.demo.disease.model.DiseaseView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/disease")
@RequiredArgsConstructor
public class DiseaseController implements DiseaseControllerApi {

    private final DiseaseService diseaseService;
    private final DiseaseMapper diseaseMapper = DiseaseMapper.INSTANCE;

    @Override
    @PostMapping()
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DiseaseResponse> createDisease(Principal principal, @RequestParam Long branchId, @RequestBody DiseaseRequest request) {
        DiseaseView diseaseView = diseaseService.create(principal, branchId, request);
        return new ResponseEntity<>(diseaseMapper.toDiseaseResponse(diseaseView), HttpStatus.CREATED);
    }

    @Override
    @GetMapping()
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<DiseaseResponse>> getAllDiseasesByDoctor(Principal principal) {
        List<DiseaseView> diseaseViews = diseaseService.getAllDiseasesByDoctor(principal);
        return ResponseEntity.ok(diseaseMapper.toDiseaseResponseList(diseaseViews));
    }

    @Override
    @GetMapping("/{id}")
    // @PreAuthorize - public access or handled by service if needed
    public ResponseEntity<DiseaseResponse> getDiseaseById(@PathVariable Long id) {
        DiseaseView diseaseView = diseaseService.getDiseaseById(id);
        return ResponseEntity.ok(diseaseMapper.toDiseaseResponse(diseaseView));
    }

    @Override
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DiseaseResponse> updateDisease(Principal principal, @PathVariable Long id, @RequestBody DiseaseRequest request) {
        DiseaseView diseaseView = diseaseService.updateDisease(principal, id, request);
        return ResponseEntity.ok(diseaseMapper.toDiseaseResponse(diseaseView));
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> deleteDisease(Principal principal, @PathVariable Long id) {
        diseaseService.deleteDisease(principal, id);
        return ResponseEntity.ok().build(); // Or ResponseEntity.noContent().build();
    }
}
