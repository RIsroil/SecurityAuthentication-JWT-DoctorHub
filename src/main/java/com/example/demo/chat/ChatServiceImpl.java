package com.example.demo.chat;

import com.example.demo.appointment.AppointmentEntity;
import com.example.demo.appointment.AppointmentRepository;
import com.example.demo.appointment.AppointmentStatus;
import com.example.demo.chat.mapper.ChatMapper;
import com.example.demo.chat.model.ChatView;
import com.example.demo.doctor.DoctorEntity;
import com.example.demo.doctor.DoctorRepository;
import com.example.demo.exception.ResourceNotFoundException; // Assuming this exists
import com.example.demo.message.mapper.MessageMapper; // Import MessageMapper
import com.example.demo.message.model.MessageResponse; // Using existing MessageResponse
import com.example.demo.message.model.MessageView;       // Import MessageView
import com.example.demo.patient.PatientEntity;
import com.example.demo.patient.PatientRepository;
import com.example.demo.user.Role; // Assuming this exists
import com.example.demo.user.UserEntity;
import com.example.demo.user.auth.AuthHelperService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AuthHelperService authHelperService;
    private final AppointmentRepository appointmentRepository;
    private final ChatMapper chatMapper = ChatMapper.INSTANCE;
    private final MessageMapper messageMapper = MessageMapper.INSTANCE; // Will be added when Message is refactored

    @Override
    @Transactional
    public ChatView createOrGetChat(Principal principal, Long doctorId) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        // Assuming only patients can initiate creating a chat this way, or this method is generic enough
        PatientEntity patient = patientRepository.findByUser_Id(user.getId());
        if (patient == null && user.getRole() == Role.PATIENT) { // Check if it's a patient without a patient record
            throw new ResourceNotFoundException("Patient record not found for user: " + user.getUsername());
        } else if (user.getRole() != Role.PATIENT) {
            // Decide if non-patients can call this. Original service didn't explicitly restrict role here for createOrGetChat
            // but contextually it's often patient-initiated. For now, allowing any authenticated user if they have a patient record.
            // If strict patient-only, throw error.
        }


        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));

        ChatEntity chat = chatRepository.findByDoctor_IdAndPatient_Id(doctor.getId(), patient.getId())
                .orElseGet(() -> {
                    ChatEntity newChat = ChatEntity.builder()
                            .doctor(doctor)
                            .patient(patient)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return chatRepository.save(newChat);
                });
        return chatMapper.toView(chat);
    }

    @Override
    public List<ChatView> getChatsByUser(Principal principal) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        List<ChatEntity> chats;

        if (user.getRole() == Role.DOCTOR) {
            DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
            if (doctor == null) throw new ResourceNotFoundException("Doctor record not found for user: " + user.getUsername());
            chats = chatRepository.findAllByDoctor_Id(doctor.getId());
        } else if (user.getRole() == Role.PATIENT) {
            PatientEntity patient = patientRepository.findByUser_Id(user.getId());
            if (patient == null) throw new ResourceNotFoundException("Patient record not found for user: " + user.getUsername());
            chats = chatRepository.findAllByPatient_Id(patient.getId());
        } else {
            throw new RuntimeException("User role not supported for fetching chats.");
        }
        return chats.stream().map(chatMapper::toView).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteChat(Principal principal, Long chatId) throws AccessDeniedException {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with ID: " + chatId));

        boolean isDoctorOfChat = chat.getDoctor().getUser().getId().equals(user.getId());
        boolean isPatientOfChat = chat.getPatient().getUser().getId().equals(user.getId());

        if (!isDoctorOfChat && !isPatientOfChat) {
            throw new AccessDeniedException("You are not authorized to delete this chat.");
        }

        List<AppointmentEntity> appointments = appointmentRepository.findAllByChat_Id(chatId);
        for (AppointmentEntity appointment : appointments) {
            appointment.setChat(null); // Decouple appointments
            appointmentRepository.save(appointment);
        }

        // Original logic for preventing deletion based on appointment status
        boolean hasPending = appointments.stream()
                .anyMatch(a -> a.getStatus() == AppointmentStatus.PENDING);
        if (hasPending) {
            throw new RuntimeException("Cannot delete chat: an associated appointment is PENDING.");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean hasFutureApproved = appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.APPROVED)
                .anyMatch(a -> a.getDate() != null && a.getTime() != null && LocalDateTime.of(a.getDate(), a.getTime()).isAfter(now));
        if (hasFutureApproved) {
            throw new RuntimeException("Cannot delete chat: an associated approved appointment is in the future.");
        }

        chatRepository.delete(chat);
    }

    @Override
    public List<MessageResponse> getMessagesByChatId(Principal principal, Long chatId) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with ID: " + chatId));

        boolean isDoctorOfChat = chat.getDoctor().getUser().getId().equals(user.getId());
        boolean isPatientOfChat = chat.getPatient().getUser().getId().equals(user.getId());

        if (!isDoctorOfChat && !isPatientOfChat) {
            throw new RuntimeException("You are not authorized to view messages for this chat.");
        }

        List<MessageView> messageViews = messageMapper.toViewList(chat.getMessages());
        return messageMapper.toMessageResponseList(messageViews);
    }
}
