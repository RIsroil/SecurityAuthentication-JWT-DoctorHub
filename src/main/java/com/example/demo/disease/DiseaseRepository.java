package com.example.demo.disease;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiseaseRepository extends JpaRepository<DiseaseEntity, Long> {
    List<DiseaseEntity> findAllByBranch_IdIn(List<Long> branchIds);

}
