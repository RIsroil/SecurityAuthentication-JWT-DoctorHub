package com.example.demo.specialization;

import com.example.demo.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpecializationService {
    private final SpecializationRepository specializationRepository;

    public void create(RequestSpecialization requestSpecialization) {
        String name = requestSpecialization.getSpecializationName();

        if (specializationRepository.findBySpecializationName(name).isPresent()) {
            throw new DuplicateResourceException("This Specialization already exists.");
        }
        SpecializationEntity entity = new SpecializationEntity();
        entity.setSpecializationName(name);

        specializationRepository.save(entity);
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
        specializationRepository.save(entity);
    }
}
