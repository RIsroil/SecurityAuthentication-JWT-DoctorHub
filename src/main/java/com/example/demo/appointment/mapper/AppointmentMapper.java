package com.example.demo.appointment.mapper;

import com.example.demo.appointment.AppointmentEntity;
import com.example.demo.appointment.model.AppointmentView;
import com.example.demo.disease.DiseaseEntity;
import com.example.demo.disease.mapper.DiseaseMapper; // Import DiseaseMapper
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {DiseaseMapper.class}) // Add DiseaseMapper to uses
public interface AppointmentMapper {
    AppointmentMapper INSTANCE = Mappers.getMapper(AppointmentMapper.class);

    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "patient.user.fullName", target = "patientName")
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "doctor.user.fullName", target = "doctorName")
    @Mapping(source = "branch.id", target = "branchId")
    @Mapping(source = "branch.branchName", target = "branchName")
    @Mapping(source = "chat.id", target = "chatId")
    @Mapping(source = "disease.id", target = "diseaseId")
    @Mapping(source = "disease.diseaseName", target = "diseaseName")
    // Use the method from DiseaseMapper via its @Named qualifier if preferred,
    // or let MapStruct resolve it if DiseaseEntity -> String mapping for price is unique in DiseaseMapper.
    // For clarity, let's assume DiseaseMapper provides a method that can be directly used for DiseaseEntity -> String.
    // If DiseaseMapper's method is `formatPriceWithCurrency(DiseaseEntity entity)`,
    // MapStruct will use it if the target type is String and source is DiseaseEntity.
    // Or, more explicitly:
    @Mapping(source = "disease", target = "price", qualifiedByName = {"DiseaseMapper", "formatPriceWithCurrency"})
    AppointmentView toView(AppointmentEntity entity);

    // The local formatPrice method is no longer needed as we use DiseaseMapper's version.
    // @Named("formatPrice")
    // default String formatPrice(DiseaseEntity disease) {
    //     if (disease == null || disease.getPrice() == null || disease.getCurrency() == null) {
    //         return null;
    //     }
    //     return disease.getPrice() + " " + disease.getCurrency().name();
    // }
}
