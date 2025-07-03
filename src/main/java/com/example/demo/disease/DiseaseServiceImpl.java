package com.example.demo.disease;

import com.example.demo.branch.BranchEntity;
import com.example.demo.branch.BranchRepository;
import com.example.demo.disease.mapper.DiseaseMapper;
import com.example.demo.disease.model.DiseaseRequest;
import com.example.demo.disease.model.DiseaseView;
import com.example.demo.doctor.DoctorEntity;
import com.example.demo.doctor.DoctorRepository;
import com.example.demo.exception.ResourceNotFoundException; // Assuming
import com.example.demo.user.UserEntity;
import com.example.demo.user.auth.AuthHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException; // Standard
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiseaseServiceImpl implements DiseaseService {

    private final DiseaseRepository diseaseRepository;
    private final DoctorRepository doctorRepository;
    private final BranchRepository branchRepository;
    private final AuthHelperService authHelperService;
    private final DiseaseMapper diseaseMapper = DiseaseMapper.INSTANCE;

    @Override
    @Transactional
    public DiseaseView create(Principal principal, Long branchId, DiseaseRequest request) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        if (doctor == null) {
            throw new ResourceNotFoundException("Doctor not found for user: " + user.getUsername());
        }

        BranchEntity branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + branchId));

        if (!branch.getDoctorEntity().getId().equals(doctor.getId())) {
            throw new AccessDeniedException("You do not own this branch, cannot add disease.");
        }

        DiseaseEntity entity = DiseaseEntity.builder()
                .diseaseName(request.getDiseaseName())
                .price(request.getPrice())
                .currency(request.getCurrency())
                .branch(branch)
                .build();
        DiseaseEntity savedEntity = diseaseRepository.save(entity);
        return diseaseMapper.toView(savedEntity);
    }

    @Override
    public List<DiseaseView> getAllDiseasesByDoctor(Principal principal) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        if (doctor == null) {
            throw new ResourceNotFoundException("Doctor not found for user: " + user.getUsername());
        }

        List<BranchEntity> branches = branchRepository.findAllByDoctorEntityId(doctor.getId());
        List<Long> branchIds = branches.stream().map(BranchEntity::getId).collect(Collectors.toList());

        if (branchIds.isEmpty()) {
            return List.of(); // No branches, so no diseases
        }

        List<DiseaseEntity> diseases = diseaseRepository.findAllByBranch_IdIn(branchIds);
        return diseaseMapper.toViewList(diseases);
    }

    @Override
    public DiseaseView getDiseaseById(Long id) {
        DiseaseEntity disease = diseaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disease not found with ID: " + id));
        // Add authorization if only specific users can fetch any disease by ID
        return diseaseMapper.toView(disease);
    }

    @Override
    @Transactional
    public DiseaseView updateDisease(Principal principal, Long id, DiseaseRequest request) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        if (doctor == null) {
            throw new ResourceNotFoundException("Doctor not found for user: " + user.getUsername());
        }

        DiseaseEntity disease = diseaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disease not found with ID: " + id));

        if (!disease.getBranch().getDoctorEntity().getId().equals(doctor.getId())) {
            throw new AccessDeniedException("You do not own this disease, cannot update.");
        }

        if (request.getDiseaseName() != null) {
            disease.setDiseaseName(request.getDiseaseName());
        }
        if (request.getPrice() != null) {
            disease.setPrice(request.getPrice());
        }
        if (request.getCurrency() != null) {
            disease.setCurrency(request.getCurrency());
        }
        DiseaseEntity updatedDisease = diseaseRepository.save(disease);
        return diseaseMapper.toView(updatedDisease);
    }

    @Override
    @Transactional
    public void deleteDisease(Principal principal, Long id) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        if (doctor == null) {
            throw new ResourceNotFoundException("Doctor not found for user: " + user.getUsername());
        }

        DiseaseEntity disease = diseaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disease not found with ID: " + id));

        if (!disease.getBranch().getDoctorEntity().getId().equals(doctor.getId())) {
            throw new AccessDeniedException("You do not own this disease, cannot delete.");
        }

        diseaseRepository.delete(disease);
    }
}
