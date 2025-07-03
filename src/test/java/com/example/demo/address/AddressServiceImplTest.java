package com.example.demo.address;

import com.example.demo.address.location.GeocodingService;
import com.example.demo.address.mapper.AddressMapper;
import com.example.demo.address.model.AddressRequest;
import com.example.demo.address.model.AddressView;
import com.example.demo.exception.ResourceNotFoundException; // Assuming this exists
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private GeocodingService geocodingService;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    private AddressRequest addressRequest;
    private AddressEntity addressEntity;
    private AddressView addressView;

    @BeforeEach
    void setUp() {
        addressRequest = new AddressRequest();
        addressRequest.setAddressName("1600 Amphitheatre Parkway, Mountain View, CA");
        // No location link initially, let geocoding handle it

        addressEntity = new AddressEntity();
        addressEntity.setId(1L);
        addressEntity.setAddressName("1600 Amphitheatre Parkway, Mountain View, CA");
        addressEntity.setLatitude(37.422);
        addressEntity.setLongitude(-122.084);
        addressEntity.setAddressLocationLink("https://www.google.com/maps/search/?api=1&query=37.422,-122.084");

        addressView = AddressView.builder()
                .id(1L)
                .addressName("1600 Amphitheatre Parkway, Mountain View, CA")
                .latitude(37.422)
                .longitude(-122.084)
                .addressLocationLink("https://www.google.com/maps/search/?api=1&query=37.422,-122.084")
                .build();

        // Make AddressMapper a real instance for these tests as it's simple
        // and helps verify the mapping if not overly complex.
        // Or mock it like other dependencies. For consistency, let's mock it.
        // For AddressMapper.INSTANCE, if it's used directly in Impl, that needs specific handling.
        // The Impl was: private final AddressMapper addressMapper = AddressMapper.INSTANCE;
        // This means @Mock AddressMapper won't be used by the @InjectMocks instance.
        // SOLUTION: Inject AddressMapper via constructor in AddressServiceImpl.
        // For now, I'll assume AddressServiceImpl is refactored to take AddressMapper in constructor.
        // If not, tests for mapper interactions would fail or need Powermock/reflection.
        // Let's assume constructor injection for AddressMapper for this test.
    }

    private void setupMocksForSuccessfulGeocoding() {
        when(geocodingService.getCoordinatesFromAddress(anyString())).thenReturn(new double[]{37.422, -122.084});
    }

    @Test
    void create_whenLocationLinkNull_shouldUseGeocodingAndSave() {
        setupMocksForSuccessfulGeocoding();
        when(addressRepository.save(any(AddressEntity.class))).thenReturn(addressEntity);
        when(addressMapper.toView(addressEntity)).thenReturn(addressView);
        // Simulating constructor injection for mapper:
        // addressService = new AddressServiceImpl(addressRepository, geocodingService, addressMapper);


        ResponseEntity<?> response = addressService.create(addressRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(addressView, response.getBody());
        verify(geocodingService, times(1)).getCoordinatesFromAddress(addressRequest.getAddressName());
        verify(addressRepository, times(1)).save(any(AddressEntity.class));
        verify(addressMapper, times(1)).toView(addressEntity);
    }

    @Test
    void create_whenLocationLinkProvided_shouldUseLinkAndSave() {
        addressRequest.setAddressLocationLink("https://www.google.com/maps/search/?api=1&query=37.0, -122.0");
        // No geocoding needed
        when(addressRepository.save(any(AddressEntity.class))).thenReturn(addressEntity);
        when(addressMapper.toView(addressEntity)).thenReturn(addressView);
        // addressService = new AddressServiceImpl(addressRepository, geocodingService, addressMapper);


        ResponseEntity<?> response = addressService.create(addressRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(addressView, response.getBody());
        verify(geocodingService, never()).getCoordinatesFromAddress(anyString());
        verify(addressRepository, times(1)).save(any(AddressEntity.class));
        verify(addressMapper, times(1)).toView(addressEntity);
    }

    @Test
    void create_whenLocationLinkInvalid_shouldReturnBadRequest() {
        addressRequest.setAddressLocationLink("http://invalidmaps.com/123");
        // addressService = new AddressServiceImpl(addressRepository, geocodingService, addressMapper);

        ResponseEntity<?> response = addressService.create(addressRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Location link must start with https://www.google.com/maps"));
        verify(addressRepository, never()).save(any(AddressEntity.class));
    }


    @Test
    void getAllAddresses_shouldReturnListOfViews() {
        AddressEntity entity2 = new AddressEntity(); // setup entity2
        AddressView view2 = AddressView.builder().build(); // setup view2
        List<AddressEntity> entities = Arrays.asList(addressEntity, entity2);
        List<AddressView> views = Arrays.asList(addressView, view2);

        when(addressRepository.findAll()).thenReturn(entities);
        when(addressMapper.toView(addressEntity)).thenReturn(addressView);
        when(addressMapper.toView(entity2)).thenReturn(view2);
        // addressService = new AddressServiceImpl(addressRepository, geocodingService, addressMapper);

        List<AddressView> result = addressService.getAllAddresses();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(addressRepository, times(1)).findAll();
        verify(addressMapper, times(2)).toView(any(AddressEntity.class)); // Called for each entity
    }

    @Test
    void update_shouldUpdateAndReturnView_whenExists() {
        AddressRequest updateRequest = new AddressRequest();
        updateRequest.setAddressName("New Address Name");
        // Assume geocoding will be called for the new name
        setupMocksForSuccessfulGeocoding();
        when(addressRepository.findById(1L)).thenReturn(Optional.of(addressEntity));
        when(addressRepository.save(any(AddressEntity.class))).thenReturn(addressEntity); // Assume it returns the same updated entity
        when(addressMapper.toView(addressEntity)).thenReturn(addressView); // Mapper returns the view
        // addressService = new AddressServiceImpl(addressRepository, geocodingService, addressMapper);


        ResponseEntity<AddressView> response = addressService.update(1L, updateRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(addressView, response.getBody()); // Check if the view is returned
        verify(addressRepository, times(1)).findById(1L);
        verify(addressRepository, times(1)).save(any(AddressEntity.class));
        verify(addressMapper, times(1)).toView(addressEntity);
        verify(geocodingService, times(1)).getCoordinatesFromAddress("New Address Name");
    }

    @Test
    void update_shouldThrowRuntimeException_whenNotFound() {
        AddressRequest updateRequest = new AddressRequest();
        updateRequest.setAddressName("New Name");
        when(addressRepository.findById(2L)).thenReturn(Optional.empty());
        // addressService = new AddressServiceImpl(addressRepository, geocodingService, addressMapper);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            addressService.update(2L, updateRequest);
        });
        assertEquals("Address not found", exception.getMessage());
        verify(addressRepository, times(1)).findById(2L);
        verify(addressRepository, never()).save(any());
    }


    @Test
    void deleteAddress_shouldCallRepositoryDelete_whenExists() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(addressEntity));
        doNothing().when(addressRepository).delete(addressEntity);
        // addressService = new AddressServiceImpl(addressRepository, geocodingService, addressMapper);

        addressService.deleteAddress(1L);

        verify(addressRepository, times(1)).findById(1L);
        verify(addressRepository, times(1)).delete(addressEntity);
    }

    @Test
    void deleteAddress_shouldThrowRuntimeException_whenNotExists() {
        when(addressRepository.findById(2L)).thenReturn(Optional.empty());
        // addressService = new AddressServiceImpl(addressRepository, geocodingService, addressMapper);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            addressService.deleteAddress(2L);
        });
        assertEquals("Address not found", exception.getMessage());
        verify(addressRepository, times(1)).findById(2L);
        verify(addressRepository, never()).delete(any());
    }

    @Test
    void getById_shouldReturnView_whenExists() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(addressEntity));
        when(addressMapper.toView(addressEntity)).thenReturn(addressView);
        // addressService = new AddressServiceImpl(addressRepository, geocodingService, addressMapper);

        AddressView result = addressService.getById(1L);

        assertNotNull(result);
        assertEquals(addressView, result);
        verify(addressRepository, times(1)).findById(1L);
        verify(addressMapper, times(1)).toView(addressEntity);
    }

    @Test
    void getById_shouldThrowRuntimeException_whenNotExists() {
        when(addressRepository.findById(2L)).thenReturn(Optional.empty());
        // addressService = new AddressServiceImpl(addressRepository, geocodingService, addressMapper);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            addressService.getById(2L);
        });
         assertEquals("Address not found", exception.getMessage());
        verify(addressRepository, times(1)).findById(2L);
        verify(addressMapper, never()).toView(any());
    }
}
