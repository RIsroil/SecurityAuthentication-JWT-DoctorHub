package com.example.demo.disease;

import com.example.demo.branch.BranchEntity;
import com.example.demo.branch.BranchRepository;
import com.example.demo.disease.model.DiseaseRequest;
import com.example.demo.disease.model.DiseaseResponse;
import com.example.demo.doctor.DoctorEntity;
import com.example.demo.doctor.DoctorRepository;
import com.example.demo.user.UserEntity;
import com.example.demo.user.UserRepository;
import com.example.demo.user.auth.AuthHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiseaseService {
    private final DiseaseRepository diseaseRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final BranchRepository branchRepository;
    private final AuthHelperService authHelperService;

    public DiseaseResponse create(Principal principal,Long branchId, DiseaseRequest request) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        BranchEntity branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        if (!branch.getDoctorEntity().getId().equals(doctor.getId())) {
            throw new RuntimeException("You do not own this branch");
        }

        DiseaseEntity entity = DiseaseEntity.builder()
                .diseaseName(request.getDiseaseName())
                .price(request.getPrice())
                .branch(branch)
                .build();
        diseaseRepository.save(entity);
        return mapToResponse(entity);
    }

    public List<DiseaseResponse> getAllDiseases(Principal principal) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);

        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        List<BranchEntity> branches = branchRepository.findAllByDoctorEntityId(doctor.getId());

        List<Long> branchIds = branches.stream()
                .map(BranchEntity::getId)
                .toList();

        List<DiseaseEntity> diseases = diseaseRepository.findAllByBranch_IdIn(branchIds);

        return diseases.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DiseaseResponse getById(Long id) {
        DiseaseEntity disease = diseaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disease not found with id: " + id));

        return mapToResponse(disease);
    }

    public DiseaseResponse updateDisease(Principal principal, Long id, DiseaseRequest request){
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        DiseaseEntity disease = diseaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disease not found with id: " + id));

        if (!disease.getBranch().getDoctorEntity().getId().equals(doctor.getId())) {
            throw new RuntimeException("You do not own this disease");
        }

        if (request.getDiseaseName() != null) disease.setDiseaseName(request.getDiseaseName());
        if (request.getPrice() != null) disease.setPrice(request.getPrice());
        diseaseRepository.save(disease);
        return mapToResponse(disease);
    }

    public void delete(Principal principal, Long id) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());

        DiseaseEntity disease = diseaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disease not found with id: " + id));

        if (!disease.getBranch().getDoctorEntity().getId().equals(doctor.getId())) {
            throw new RuntimeException("You do not own this disease");
        }

        diseaseRepository.delete(disease);
    }

    public DiseaseResponse mapToResponse(DiseaseEntity entity) {
        return DiseaseResponse.builder()
                .id(entity.getId())
                .branchId(entity.getBranch().getId())
                .branchName(entity.getBranch().getBranchName())
                .diseaseName(entity.getDiseaseName())
                .price(entity.getPrice())
                .build();
    }

}
