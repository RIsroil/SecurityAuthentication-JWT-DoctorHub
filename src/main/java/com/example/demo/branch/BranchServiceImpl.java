package com.example.demo.branch;

import com.example.demo.address.location.GeocodingService;
import com.example.demo.branch.mapper.BranchMapper;
import com.example.demo.branch.model.BranchRequest;
import com.example.demo.branch.model.BranchUpdateRequest;
import com.example.demo.branch.model.BranchView;
// import com.example.demo.disease.mapper.DiseaseMapper; // This will be added later
// import com.example.demo.disease.model.DiseaseResponse; // This will be added later
import com.example.demo.doctor.DoctorEntity;
import com.example.demo.doctor.DoctorRepository;
import com.example.demo.user.UserEntity;
import com.example.demo.user.auth.AuthHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final GeocodingService geocodingService;
    private final DoctorRepository doctorRepository;
    private final BranchRepository branchRepository;
    private final AuthHelperService authHelperService;
    // private final DiseaseMapper diseaseMapper; // This will be added later
    private final BranchMapper branchMapper = BranchMapper.INSTANCE;


    @Override
    public ResponseEntity<?> createBranch(Principal principal, BranchRequest request) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        if (doctor == null) throw new RuntimeException("Doctor not found");

        BranchEntity branch = new BranchEntity();
        branch.setBranchName(request.getBranchName());
        branch.setBranchCity(request.getBranchCity());
        branch.setBranchRegion(request.getBranchRegion());
        branch.setDoctorEntity(doctor);
        if (!request.getBranchLocationLink().startsWith("https://www.google.com/maps")) {
            return ResponseEntity.badRequest().body("Location link must be a Google Maps link");
        }
        try {
            double[] coords = geocodingService.getCoordinatesFromAddress(request.getBranchLocationLink());
            branch.setLatitude(coords[0]);
            branch.setLongitude(coords[1]);
            branch.setBranchLocationLink(request.getBranchLocationLink());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Invalid Google Maps location link");
        }
        branchRepository.save(branch);
        return ResponseEntity.ok(branchMapper.toView(branch));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchView> getMyBranches(Principal principal) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        if (doctor == null) throw new RuntimeException("Doctor not found");

        List<BranchEntity> branches = branchRepository.findAllByDoctorEntityId(doctor.getId());
        return branches.stream()
                .map(branchMapper::toView)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<?> deleteBranch(Principal principal, Long id) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        BranchEntity branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        if (!branch.getDoctorEntity().getId().equals(doctor.getId())) {
            throw new RuntimeException("You cannot delete this branch");
        }

        branchRepository.delete(branch);
        return ResponseEntity.ok("Branch successfully deleted");
    }

    @Override
    public ResponseEntity<?> updateBranch(Principal principal, Long id, BranchUpdateRequest request) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        BranchEntity branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        if (!branch.getDoctorEntity().getId().equals(doctor.getId())) {
            throw new RuntimeException("You cannot update this branch");
        }

        if (request == null) {
            return ResponseEntity.badRequest().body("Update request cannot be null");
        }

        if (request.getBranchName() != null) branch.setBranchName(request.getBranchName());
        if (request.getBranchCity() != null) branch.setBranchCity(request.getBranchCity());
        if (request.getBranchRegion() != null) branch.setBranchRegion(request.getBranchRegion());
        if (request.getBranchDescription() != null) branch.setBranchDescription(request.getBranchDescription());
        if (request.getBranchImageUrl() != null) branch.setBranchImageUrl(request.getBranchImageUrl());
        if (request.getAvailableDays() != null) branch.setAvailableDays(request.getAvailableDays());

        if (request.getBranchLocationLink() != null) {
            if (!request.getBranchLocationLink().startsWith("https://www.google.com/maps")) {
                return ResponseEntity.badRequest().body("Location link must be a Google Maps link");
            } else {
                try {
                    double[] coords = geocodingService.getCoordinatesFromAddress(request.getBranchLocationLink());
                    branch.setLatitude(coords[0]);
                    branch.setLongitude(coords[1]);
                    branch.setBranchLocationLink(request.getBranchLocationLink());
                } catch (Exception ex) {
                    return ResponseEntity.badRequest().body("Invalid Google Maps location link");
                }
            }
        }
        branchRepository.save(branch);
        return ResponseEntity.ok(branchMapper.toView(branch));
    }

    @Override
    @Transactional(readOnly = true)
    public BranchView getBranchById(Long id) {
        BranchEntity branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        return branchMapper.toView(branch);
    }
}
