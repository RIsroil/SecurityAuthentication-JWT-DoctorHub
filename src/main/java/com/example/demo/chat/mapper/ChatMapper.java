package com.example.demo.chat.mapper;

import com.example.demo.chat.ChatEntity;
import com.example.demo.chat.model.ChatView;
import com.example.demo.chat.model.ChatResponse;
import com.example.demo.doctor.DoctorEntity;
import com.example.demo.patient.PatientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChatMapper {
    ChatMapper INSTANCE = Mappers.getMapper(ChatMapper.class);

    @Mapping(source = "id", target = "chatId")
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "doctor", target = "doctorName", qualifiedByName = "formatDoctorName")
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "patient", target = "patientName", qualifiedByName = "formatPatientName")
    ChatView toView(ChatEntity entity);

    @Named("formatDoctorName")
    default String formatDoctorName(DoctorEntity doctor) {
        if (doctor == null) {
            return null;
        }
        return (doctor.getFirstname() != null ? doctor.getFirstname() : "") +
               (doctor.getLastname() != null ? " " + doctor.getLastname() : "");
    }

    @Named("formatPatientName")
    default String formatPatientName(PatientEntity patient) {
        if (patient == null) {
            return null;
        }
        return (patient.getFirstname() != null ? patient.getFirstname() : "") +
               (patient.getLastname() != null ? " " + patient.getLastname() : "");
    }

    default ChatResponse toChatResponse(ChatView view) {
        if (view == null) {
            return null;
        }
        return ChatResponse.builder()
                .chatId(view.getChatId())
                .doctorId(view.getDoctorId())
                .doctorName(view.getDoctorName())
                .patientId(view.getPatientId())
                .patientName(view.getPatientName())
                .createdAt(view.getCreatedAt())
                .build();
    }

    // For converting List<ChatView> to List<ChatResponse>
    default java.util.List<ChatResponse> toChatResponseList(java.util.List<ChatView> views) {
        if (views == null) {
            return null;
        }
        return views.stream()
                    .map(this::toChatResponse)
                    .collect(java.util.stream.Collectors.toList());
    }
}
