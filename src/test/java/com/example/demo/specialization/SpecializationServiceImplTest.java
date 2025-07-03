package com.example.demo.specialization;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.specialization.mapper.SpecializationMapper;
import com.example.demo.specialization.model.SpecializationView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecializationServiceImplTest {

    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private SpecializationMapper specializationMapper;

    @InjectMocks
    private SpecializationServiceImpl specializationService;

    private SpecializationEntity entity1;
    private SpecializationView view1;
    private RequestSpecialization request1;

    @BeforeEach
    void setUp() {
        entity1 = SpecializationEntity.builder().id(1L).specializationName("Cardiology").build();
        view1 = SpecializationView.builder().id(1L).specializationName("Cardiology").build();
        request1 = new RequestSpecialization();
        request1.setSpecializationName("Cardiology");
    }

    @Test
    void createSpecialization_shouldReturnView() {
        when(specializationRepository.save(any(SpecializationEntity.class))).thenReturn(entity1);
        when(specializationMapper.toView(any(SpecializationEntity.class))).thenReturn(view1);

        SpecializationView result = specializationService.createSpecialization(request1);

        assertNotNull(result);
        assertEquals("Cardiology", result.getSpecializationName());
        verify(specializationRepository, times(1)).save(any(SpecializationEntity.class));
        verify(specializationMapper, times(1)).toView(entity1);
    }

    @Test
    void getSpecializationById_shouldReturnView_whenExists() {
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(entity1));
        when(specializationMapper.toView(entity1)).thenReturn(view1);

        SpecializationView result = specializationService.getSpecializationById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(specializationRepository, times(1)).findById(1L);
        verify(specializationMapper, times(1)).toView(entity1);
    }

    @Test
    void getSpecializationById_shouldThrowResourceNotFound_whenNotExists() {
        when(specializationRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            specializationService.getSpecializationById(2L);
        });
        verify(specializationRepository, times(1)).findById(2L);
        verify(specializationMapper, never()).toView(any());
    }

    @Test
    void getAllSpecializations_shouldReturnListOfViews() {
        SpecializationEntity entity2 = SpecializationEntity.builder().id(2L).specializationName("Neurology").build();
        SpecializationView view2 = SpecializationView.builder().id(2L).specializationName("Neurology").build();
        List<SpecializationEntity> entities = Arrays.asList(entity1, entity2);
        List<SpecializationView> views = Arrays.asList(view1, view2);

        when(specializationRepository.findAll()).thenReturn(entities);
        when(specializationMapper.toViewList(entities)).thenReturn(views);

        List<SpecializationView> results = specializationService.getAllSpecializations();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("Cardiology", results.get(0).getSpecializationName());
        assertEquals("Neurology", results.get(1).getSpecializationName());
        verify(specializationRepository, times(1)).findAll();
        verify(specializationMapper, times(1)).toViewList(entities);
    }

    @Test
    void updateSpecialization_shouldReturnUpdatedView_whenExists() {
        RequestSpecialization updateRequest = new RequestSpecialization();
        updateRequest.setSpecializationName("Cardio Health");

        SpecializationEntity updatedEntity = SpecializationEntity.builder().id(1L).specializationName("Cardio Health").build();
        SpecializationView updatedView = SpecializationView.builder().id(1L).specializationName("Cardio Health").build();

        when(specializationRepository.findById(1L)).thenReturn(Optional.of(entity1));
        when(specializationRepository.save(any(SpecializationEntity.class))).thenReturn(updatedEntity);
        when(specializationMapper.toView(updatedEntity)).thenReturn(updatedView);

        SpecializationView result = specializationService.updateSpecialization(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Cardio Health", result.getSpecializationName());
        verify(specializationRepository, times(1)).findById(1L);
        verify(specializationRepository, times(1)).save(any(SpecializationEntity.class));
        verify(specializationMapper, times(1)).toView(updatedEntity);
    }

    @Test
    void updateSpecialization_shouldThrowResourceNotFound_whenNotExists() {
        RequestSpecialization updateRequest = new RequestSpecialization();
        updateRequest.setSpecializationName("Cardio Health");
        when(specializationRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            specializationService.updateSpecialization(2L, updateRequest);
        });
        verify(specializationRepository, times(1)).findById(2L);
        verify(specializationRepository, never()).save(any());
        verify(specializationMapper, never()).toView(any());
    }

    @Test
    void deleteSpecialization_shouldCallRepositoryDelete_whenExists() {
        when(specializationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(specializationRepository).deleteById(1L);

        specializationService.deleteSpecialization(1L);

        verify(specializationRepository, times(1)).existsById(1L);
        verify(specializationRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteSpecialization_shouldThrowResourceNotFound_whenNotExists() {
        when(specializationRepository.existsById(2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            specializationService.deleteSpecialization(2L);
        });
        verify(specializationRepository, times(1)).existsById(2L);
        verify(specializationRepository, never()).deleteById(anyLong());
    }
}
