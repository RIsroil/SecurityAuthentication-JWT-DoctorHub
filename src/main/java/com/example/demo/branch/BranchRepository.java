package com.example.demo.branch;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BranchRepository extends JpaRepository<BranchEntity, Long> {
    List<BranchEntity> findAllByDoctorEntityId(Long doctorId);
}
