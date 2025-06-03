package com.example.demo.address;

import com.example.demo.address.location.GeocodingService;
import com.example.demo.address.model.AddressRequest;
import com.example.demo.address.model.AddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final GeocodingService geocodingService;

    public AddressResponse mapToResponse(AddressEntity entity) {
        AddressResponse response = new AddressResponse();
        response.setId(entity.getId());
        response.setAddressName(entity.getAddressName());
        response.setAddressLocationLink(entity.getAddressLocationLink());
        response.setLatitude(entity.getLatitude());
        response.setLongitude(entity.getLongitude());
        return response;
    }

    public List<AddressResponse> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ResponseEntity<AddressResponse> create(AddressRequest request) {
        // Geocodingdan latitude va longitude olish
        if(request.getAddressLocationLink() == null) {
            double[] coordinates = geocodingService.getCoordinatesFromAddress(request.getAddressName());
            double latitude = coordinates[0];
            double longitude = coordinates[1];
            String link = "https://www.google.com/maps/search/?api=1&query=" +
                    latitude + "," + longitude;


            // Yangi addressni saqlash
            AddressEntity addressEntity = new AddressEntity();
            addressEntity.setAddressName(request.getAddressName());
            addressEntity.setAddressLocationLink(link);
            addressEntity.setLatitude(latitude);  // Latitude va Longitude ni saqlash
            addressEntity.setLongitude(longitude);

            AddressEntity saved = addressRepository.save(addressEntity);
            return ResponseEntity.ok(mapToResponse(saved));
        }else{
            AddressEntity addressEntity = new AddressEntity();
            addressEntity.setAddressName(request.getAddressName());
            addressEntity.setAddressLocationLink(request.getAddressLocationLink());

            AddressEntity saved = addressRepository.save(addressEntity);
            return ResponseEntity.ok(mapToResponse(saved));
        }
    }

    public ResponseEntity<AddressResponse> update(Long id, AddressRequest request) {
        AddressEntity addressEntity = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // AddressName berilsa, faqat uni yangilash
        if (request.getAddressName() != null && !request.getAddressName().isBlank()) {
            addressEntity.setAddressName(request.getAddressName());
        }

        double[] coordinates;
        String finalLink;

        try {
            // Agar addressName mavjud bo‘lsa, geokodlash
            if (addressEntity.getAddressName() != null && !addressEntity.getAddressName().isBlank()) {
                coordinates = geocodingService.getCoordinatesFromAddress(addressEntity.getAddressName());
                finalLink = "https://www.google.com/maps/search/?api=1&query=" + coordinates[0] + "," + coordinates[1];
            } else if (request.getAddressLocationLink() != null && !request.getAddressLocationLink().isBlank()) {
                // Agar AddressName berilmagan bo‘lsa, LocationLinkdan foydalaning
                coordinates = geocodingService.getCoordinatesFromAddress(request.getAddressLocationLink());
                finalLink = request.getAddressLocationLink();
            } else {
                throw new RuntimeException("Neither address name nor location link is valid");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving coordinates: " + e.getMessage());
        }

        // 🔁 Hammasini yangilash
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
}


