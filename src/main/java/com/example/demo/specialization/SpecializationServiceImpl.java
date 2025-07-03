package com.example.demo.specialization;

import com.example.demo.exception.ResourceNotFoundException; // Assuming custom exception
import com.example.demo.specialization.mapper.SpecializationMapper;
import com.example.demo.specialization.model.SpecializationView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecializationServiceImpl implements SpecializationService {
    private final SpecializationRepository specializationRepository;
    private final SpecializationMapper specializationMapper = SpecializationMapper.INSTANCE;

    @Override
    @Transactional
    public SpecializationView createSpecialization(RequestSpecialization requestSpecialization) {
        String name = requestSpecialization.getSpecializationName();
        // System.out.println("Yaratilayotgan specialization: " + name); // Logging can be done via a proper logger

        SpecializationEntity entity = new SpecializationEntity();
        entity.setSpecializationName(name);
        // Consider checking if specialization with this name already exists

        SpecializationEntity savedEntity = specializationRepository.save(entity);
        // System.out.println("Bazaga saqlandi: " + savedEntity.getSpecializationName());
        return specializationMapper.toView(savedEntity);
    }

    @Override
    @Transactional
    public void deleteSpecialization(Long id) {
        if (!specializationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Specialization not found with ID: " + id);
        }
        specializationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public SpecializationView getSpecializationById(Long id) {
        SpecializationEntity entity = specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found with ID: " + id));
        return specializationMapper.toView(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecializationView> getAllSpecializations() {
        return specializationMapper.toViewList(specializationRepository.findAll());
    }

    @Override
    @Transactional
    public SpecializationView updateSpecialization(Long id, RequestSpecialization requestSpecialization) {
        SpecializationEntity entity = specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found with ID: " + id + " for update."));
        entity.setSpecializationName(requestSpecialization.getSpecializationName());
        SpecializationEntity updated = specializationRepository.save(entity);
        return specializationMapper.toView(updated);
    }
}
