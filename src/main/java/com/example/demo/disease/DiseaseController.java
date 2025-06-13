package com.example.demo.disease;

import com.example.demo.disease.model.DiseaseRequest;
import com.example.demo.disease.model.DiseaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/disease")
@RequiredArgsConstructor
public class DiseaseController {

    private final DiseaseService diseaseService;

    @PostMapping()
    public DiseaseResponse create(Principal principal,Long branchId, @RequestBody DiseaseRequest request){
        return diseaseService.create(principal, branchId, request);
    }

    @GetMapping()
    public List<DiseaseResponse> getAll(Principal principal){
        return diseaseService.getAllDiseases(principal);
    }

    @GetMapping("/{id}")
    public DiseaseResponse getById(@PathVariable Long id){
        return diseaseService.getById(id);
    }

    @PatchMapping("/{id}")
    public DiseaseResponse update(Principal principal, @PathVariable Long id, @RequestBody DiseaseRequest request){
        return diseaseService.updateDisease(principal, id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(Principal principal,@PathVariable Long id){
        diseaseService.delete(principal, id);
    }
}
