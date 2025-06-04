package com.example.demo.branch;

import com.example.demo.address.location.GeocodingService;
import com.example.demo.branch.model.BranchRequest;
import com.example.demo.branch.model.BranchResponse;
import com.example.demo.branch.model.BranchUpdateRequest;
import com.example.demo.doctor.DoctorEntity;
import com.example.demo.doctor.DoctorRepository;
import com.example.demo.jwt.JwtService;
import com.example.demo.user.UserEntity;
import com.example.demo.user.UserRepository;
import com.example.demo.user.auth.AuthHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final GeocodingService geocodingService;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final JwtService jwtService;
    private final BranchRepository branchRepository;
    private final AuthHelperService authHelperService;

    public ResponseEntity<?> createBranch(String accessToken, BranchRequest request) {
        UserEntity user = authHelperService.getUserFromToken(accessToken);
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());

        BranchEntity branch = new BranchEntity();
        if (request.getBranchName()!=null) {branch.setBranchName(request.getBranchName());}
            else {return ResponseEntity.badRequest().body("Branch name can not be empty");}

        if (request.getBranchCity()!=null) {branch.setBranchCity(request.getBranchCity());}
            else {return ResponseEntity.badRequest().body("Branch city can not be empty");}

        if (request.getBranchRegion()!=null) {branch.setBranchRegion(request.getBranchRegion());}
            else {ResponseEntity.badRequest().body("Branch region can not be empty");}
        branch.setDoctorEntity(doctor);

        if (!request.getBranchLocationLink().startsWith("https://www.google.com/maps")) return ResponseEntity.badRequest().body("Location link must be a Google Maps link");
        try {
            double[] coords = geocodingService.getCoordinatesFromAddress(request.getBranchLocationLink());
            branch.setLatitude(coords[0]);
            branch.setLongitude(coords[1]);
            branch.setBranchLocationLink(request.getBranchLocationLink());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Invalid Google Maps location link");
        }
        branchRepository.save(branch);
        return mapToResponse(branch);
    }

    public List<BranchResponse> getMyBranches(String accessToken) {
        UserEntity user = authHelperService.getUserFromToken(accessToken);
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());

        List<BranchEntity> branches = branchRepository.findAllByDoctorEntityId(doctor.getId());

        return branches.stream()
                .map(branch -> BranchResponse.builder()
                        .branchId(branch.getId())
                        .branchName(branch.getBranchName())
                        .branchCity(branch.getBranchCity())
                        .branchRegion(branch.getBranchRegion())
                        .branchLocationLink(branch.getBranchLocationLink())
                        .branchImageUrl(branch.getBranchImageUrl())
                        .branchDescription(branch.getBranchDescription())
                        .doctorId(branch.getDoctorEntity().getId())
                        .atDate(branch.getAtDate())
                        .build())
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> deleteBranch(String accessToken, Long id) {
        UserEntity user = authHelperService.getUserFromToken(accessToken);;
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        BranchEntity branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        if (!branch.getId().equals(doctor.getId())) {
            throw new RuntimeException("You can not delete this branch");
        }
        branchRepository.delete(branch);
        return ResponseEntity.ok("Branch muvaffaqiyatli o'chirildi");
    }

    public ResponseEntity<?> updateBranch(String accessToken, Long id, BranchUpdateRequest request) {
        UserEntity user = authHelperService.getUserFromToken(accessToken);;
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        BranchEntity branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        if (!branch.getDoctorEntity().getId().equals(doctor.getId())) {
            throw new RuntimeException("You can not update this branch");
        }

        if (request.getBranchName() != null) branch.setBranchName(request.getBranchName ());
        if (request.getBranchCity() != null) branch.setBranchCity(request.getBranchCity());
        if (request.getBranchRegion() != null) branch.setBranchRegion(request.getBranchRegion());
        if (request.getBranchDescription() != null) branch.setBranchDescription(request.getBranchDescription());
        if (request.getBranchImageUrl() != null) branch.setBranchImageUrl(request.getBranchImageUrl());
        if (request.getBranchDescription() != null) branch.setBranchDescription(request.getBranchDescription());
        if (request.getAtDate() != null) branch.setAtDate(request.getAtDate());

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
        return mapToResponse(branch);
    }

    public BranchResponse getBranchById(Long id) {
        BranchEntity branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        return BranchResponse.builder()
                .branchId(branch.getId())
                .branchName(branch.getBranchName())
                .branchCity(branch.getBranchCity())
                .branchRegion(branch.getBranchRegion())
                .branchLocationLink(branch.getBranchLocationLink())
                .branchImageUrl(branch.getBranchImageUrl())
                .branchDescription(branch.getBranchDescription())
                .atDate(branch.getAtDate())
                .doctorId(branch.getDoctorEntity().getId())
                .build();
    }

    private ResponseEntity<BranchResponse> mapToResponse(BranchEntity branch){
        return ResponseEntity.ok(BranchResponse.builder()
                .branchId(branch.getId())
                .branchName(branch.getBranchName())
                .branchCity(branch.getBranchCity())
                .branchRegion(branch.getBranchRegion())
                .branchLocationLink(branch.getBranchLocationLink())
                .branchImageUrl(branch.getBranchImageUrl())
                .branchDescription(branch.getBranchDescription())
                .atDate(branch.getAtDate())
                .doctorId(branch.getDoctorEntity().getId())
                .build());
    }
}
