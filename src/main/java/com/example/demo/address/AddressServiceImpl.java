package com.example.demo.address;

import com.example.demo.address.location.GeocodingService;
import com.example.demo.address.mapper.AddressMapper;
import com.example.demo.address.model.AddressRequest;
import com.example.demo.address.model.AddressView;
import com.example.demo.address.mapper.AddressMapper; // Keep this import for the field
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final GeocodingService geocodingService;
    private final AddressMapper addressMapper; // Removed INSTANCE initialization

    // Constructor for DI
    public AddressServiceImpl(AddressRepository addressRepository, GeocodingService geocodingService, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.geocodingService = geocodingService;
        this.addressMapper = addressMapper;
    }

    @Override
    public ResponseEntity<?> create(AddressRequest request) {
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setAddressName(request.getAddressName());

        if (request.getAddressLocationLink() == null || request.getAddressLocationLink().isBlank()) {
            double[] coordinates = geocodingService.getCoordinatesFromAddress(request.getAddressName());
            addressEntity = getAddressEntity(request, coordinates);
        } else {
            if (!request.getAddressLocationLink().startsWith("https://www.google.com/maps")) {
                return ResponseEntity.badRequest().body("Location link must start with https://www.google.com/maps");
            }
            addressEntity.setAddressLocationLink(request.getAddressLocationLink());
        }

        AddressEntity saved = addressRepository.save(addressEntity);
        return ResponseEntity.ok(addressMapper.toView(saved));
    }

    @NotNull
    private AddressEntity getAddressEntity(AddressRequest request, double[] coordinates) {
        double latitude = coordinates[0];
        double longitude = coordinates[1];
        String link = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;

        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setAddressName(request.getAddressName());
        addressEntity.setAddressLocationLink(link);
        addressEntity.setLatitude(latitude);
        addressEntity.setLongitude(longitude);
        return addressEntity;
    }

    @Override
    public List<AddressView> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(addressMapper::toView)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<AddressView> update(Long id, AddressRequest request) {
        AddressEntity addressEntity = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (request.getAddressName() != null && !request.getAddressName().isBlank()) {
            addressEntity.setAddressName(request.getAddressName());
        }

        double[] coordinates;
        String finalLink;
        try {
            if (addressEntity.getAddressName() != null && !addressEntity.getAddressName().isBlank()) {
                coordinates = geocodingService.getCoordinatesFromAddress(addressEntity.getAddressName());
                finalLink = "https://www.google.com/maps/search/?api=1&query=" + coordinates[0] + "," + coordinates[1];
            } else if (request.getAddressLocationLink() != null && !request.getAddressLocationLink().isBlank()) {
                coordinates = geocodingService.getCoordinatesFromAddress(request.getAddressLocationLink());
                finalLink = request.getAddressLocationLink();
            } else {
                throw new RuntimeException("Neither address name nor location link is valid");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving coordinates: " + e.getMessage());
        }
        addressEntity.setLatitude(coordinates[0]);
        addressEntity.setLongitude(coordinates[1]);
        addressEntity.setAddressLocationLink(finalLink);

        AddressEntity updated = addressRepository.save(addressEntity);
        return ResponseEntity.ok(addressMapper.toView(updated));
    }

    @Override
    public void deleteAddress(Long id) {
        AddressEntity entity = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        addressRepository.delete(entity);
    }

    @Override
    public AddressView getById(Long id) {
        return addressMapper.toView(addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found")));
    }
}
