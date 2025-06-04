package com.example.demo.address;

import com.example.demo.address.location.GeocodingService;
import com.example.demo.address.model.AddressRequest;
import com.example.demo.address.model.AddressResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final GeocodingService geocodingService;

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
        return ResponseEntity.ok(mapToResponse(saved));
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

    public List<AddressResponse> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ResponseEntity<AddressResponse> update(Long id, AddressRequest request) {
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
        return ResponseEntity.ok(mapToResponse(updated));
    }

    public void deleteAddress(Long id) {
        AddressEntity entity = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        addressRepository.delete(entity);
    }

    public AddressEntity getById(Long id) {
        return addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));
    }

    private AddressResponse mapToResponse(AddressEntity entity) {
        AddressResponse response = new AddressResponse();
        response.setId(entity.getId());
        response.setAddressName(entity.getAddressName());
        response.setAddressLocationLink(entity.getAddressLocationLink());
        response.setLatitude(entity.getLatitude());
        response.setLongitude(entity.getLongitude());
        return response;
    }
}


