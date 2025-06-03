package com.example.demo.specialization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpecializationService {
    private final SpecializationRepository specializationRepository;

    public void create(RequestSpecialization requestSpecialization) {
        String name = requestSpecialization.getSpecializationName();
        System.out.println("Yaratilayotgan specialization: " + name);

        SpecializationEntity entity = new SpecializationEntity();
        entity.setSpecializationName(name);

        specializationRepository.save(entity);

        System.out.println("Bazaga saqlandi: " + entity.getSpecializationName());
    }


    public void delete(Long id) {
        specializationRepository.deleteById(id);
    }

    public SpecializationEntity getById(Long id) {
        return specializationRepository.findById(id).orElseThrow(() -> new RuntimeException("Specialization not found."));
    }

    public Iterable<SpecializationEntity> getAll() {
        return specializationRepository.findAll();
    }

    public void update(Long id, RequestSpecialization requestSpecialization) {
        SpecializationEntity entity = specializationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        entity.setSpecializationName(requestSpecialization.getSpecializationName());
        SpecializationEntity updated = specializationRepository.save(entity);
    }
}
