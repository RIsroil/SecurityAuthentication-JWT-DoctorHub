package com.example.demo.chat;

import com.example.demo.appointment.AppointmentEntity;
import com.example.demo.appointment.AppointmentRepository;
import com.example.demo.appointment.AppointmentStatus;
import com.example.demo.chat.model.ChatResponse;
import com.example.demo.doctor.DoctorEntity;
import com.example.demo.doctor.DoctorRepository;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.message.MessageEntity;
import com.example.demo.message.model.MessageResponse;
import com.example.demo.patient.PatientEntity;
import com.example.demo.patient.PatientRepository;
import com.example.demo.user.UserEntity;
import com.example.demo.user.UserRepository;
import com.example.demo.user.auth.AuthHelperService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AuthHelperService authHelperService;
    private final AppointmentRepository appointmentRepository;

    public ChatEntity createOrGetChat(Principal principal, Long doctorId) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        PatientEntity patient = patientRepository.findByUser_Id(user.getId());

        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return chatRepository.findByDoctor_IdAndPatient_Id(doctor.getId(), patient.getId())
                .orElseGet(() -> {
                    ChatEntity newChat = ChatEntity.builder()
                            .doctor(doctor)
                            .patient(patient)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return chatRepository.save(newChat);
                });
    }


    public List<ChatResponse> getChatsByUser(Principal principal) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);

        if (user.getRole().name().equals("DOCTOR")) {
            DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId()) ;
            return chatRepository.findAllByDoctor_Id(doctor.getId())
                    .stream()
                    .map(this::toResponse)
                    .toList();

        } else if (user.getRole().name().equals("PATIENT")) {
            PatientEntity patient = patientRepository.findByUser_Id(user.getId());
            return chatRepository.findAllByPatient_Id(patient.getId())
                    .stream()
                    .map(this::toResponse)
                    .toList();

        } else {
            throw new RuntimeException("Faqat Doctor yoki Patient chatlarga ega bo‘ladi");
        }
    }

    @Transactional
    public void deleteChat(Principal principal, Long chatId) throws AccessDeniedException {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        // Verify authorization
        if (!chat.getDoctor().getUser().getId().equals(user.getId()) &&
                !chat.getPatient().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this chat");
        }

        // Handle related appointments
        List<AppointmentEntity> appointments = appointmentRepository.findAllByChat_Id(chatId);
        for (AppointmentEntity appointment : appointments) {
            appointment.setChat(null);
            appointmentRepository.save(appointment);
        }

        boolean hasPending = appointments.stream()
                .anyMatch(a -> a.getStatus() == AppointmentStatus.PENDING);
        if (hasPending) {
            throw new RuntimeException("Chatni o‘chira olmaysiz, chunki appointment PENDING holatida");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean hasFutureApproved = appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.APPROVED)
                .anyMatch(a -> {
                    LocalDateTime appointmentDateTime =
                            LocalDateTime.of(a.getDate(), a.getTime());
                    return appointmentDateTime.isAfter(now);
                });
        if (hasFutureApproved) {
            throw new RuntimeException("Chatni o‘chira olmaysiz, tasdiqlangan appointment vaqti hali kelmagan");
        }

        chatRepository.delete(chat);
    }


    public List<MessageResponse> getMessagesByChatId(Principal principal, Long chatId) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);

        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat topilmadi"));

        Long doctorUserId = chat.getDoctor().getUser().getId();
        Long patientUserId = chat.getPatient().getUser().getId();

        boolean isOwner = user.getId().equals(doctorUserId) || user.getId().equals(patientUserId);

        if (!isOwner) {
            throw new RuntimeException("Sizda bu chatga kirishga ruxsat yo‘q");
        }

        return chat.getMessages().stream()
                .map(message -> MessageResponse.builder()
                        .id( message.getId() )
                        .content( message.getContent() )
                        .senderName( message.getSender().getUsername() )
                        .timestamp( message.getTimestamp() )
                        .build())
                .toList();
    }

    public ChatResponse toResponse(ChatEntity chat) {
        return ChatResponse.builder()
                .chatId( chat.getId() )
                .doctorId( chat.getDoctor().getId() )
                .doctorName( chat.getDoctor().getFirstname() + " " + chat.getDoctor().getLastname())
                .patientId( chat.getPatient().getId() )
                .patientName( chat.getPatient().getFirstname() + " " + chat.getPatient().getLastname())
                .createdAt( chat.getCreatedAt() )
                .build();
    }
}
