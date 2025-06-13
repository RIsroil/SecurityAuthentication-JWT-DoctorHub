package com.example.demo.chat;

import com.example.demo.chat.model.ChatResponse;
import com.example.demo.doctor.DoctorEntity;
import com.example.demo.doctor.DoctorRepository;
import com.example.demo.message.model.MessageResponse;
import com.example.demo.patient.PatientEntity;
import com.example.demo.patient.PatientRepository;
import com.example.demo.user.UserEntity;
import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    public void createOrGetChat(Principal principal, Long doctorId) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User topilmadi"));

        PatientEntity patient = patientRepository.findByUser_Id(user.getId());

        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        chatRepository.findByDoctor_IdAndPatient_Id(doctor.getId(), patient.getId())
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
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User topilmadi"));

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

    public void deleteChat(Principal principal, Long chatId) {
        String username = principal.getName();
        UserEntity currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));

        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat topilmadi"));

        Long doctorUserId = chat.getDoctor().getUser().getId();
        Long patientUserId = chat.getPatient().getUser().getId();

        boolean isOwner = currentUser.getId().equals(doctorUserId) || currentUser.getId().equals(patientUserId);

        if (!isOwner) {
            throw new RuntimeException("Siz bu chatni o‘chirishga ruxsatingiz yo‘q");
        }

        chatRepository.delete(chat);
    }

    public List<MessageResponse> getMessagesByChatId(Principal principal, Long chatId) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));

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
