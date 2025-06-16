package com.example.demo.appointment;

import com.example.demo.appointment.model.AppointmentRequest;
import com.example.demo.appointment.model.AppointmentResponse;
import com.example.demo.branch.BranchEntity;
import com.example.demo.branch.BranchRepository;
import com.example.demo.chat.ChatEntity;
import com.example.demo.chat.ChatService;
import com.example.demo.disease.DiseaseEntity;
import com.example.demo.disease.DiseaseRepository;
import com.example.demo.doctor.DoctorEntity;
import com.example.demo.doctor.DoctorRepository;
import com.example.demo.message.MessageService;
import com.example.demo.message.model.MessageRequest;
import com.example.demo.patient.PatientEntity;
import com.example.demo.patient.PatientRepository;
import com.example.demo.user.UserEntity;
import com.example.demo.user.auth.AuthHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final BranchRepository branchRepository;
    private final DiseaseRepository diseaseRepository;
    private final ChatService chatService;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final AuthHelperService authHelperService;
    private final MessageService messageService;

    public AppointmentResponse create(Principal principal, Long doctorId, AppointmentRequest request) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);

        PatientEntity patient = patientRepository.findByUser_Id(user.getId());
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));

        BranchEntity branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        DiseaseEntity diseaseEntity = null;
        String customReason = request.getCustomReason();
        Long diseaseId = request.getDiseaseId();

        if (diseaseId != null && customReason != null && !customReason.isBlank()) {
            throw new RuntimeException("Faqat bitta tanlov: ya diseaseId, yoki customReason kerak");
        }

        if ((diseaseId == null) && (customReason == null || customReason.isBlank())) {
            throw new RuntimeException("Xizmat tanlanmagan yoki sabab kiritilmagan");
        }

        if (diseaseId != null) {
            diseaseEntity = diseaseRepository.findById(diseaseId)
                    .orElseThrow(() -> new RuntimeException("Disease topilmadi"));
        }
        ChatEntity chat = chatService.createOrGetChat(principal, doctorId);

        AppointmentEntity appointmentEntity = AppointmentEntity.builder()
                .doctor(doctor)
                .patient(patient)
                .branch(branch)
                .date(request.getDate())
                .time(request.getTime())
                .status(AppointmentStatus.PENDING)
                .chat(chat)
                .disease(diseaseEntity)
                .customReason(request.getCustomReason())
                .build();

        appointmentRepository.save(appointmentEntity);

        String messageText = buildAppointmentMessage(appointmentEntity);
        messageService.sendMessage(principal, chat.getId(), new MessageRequest(messageText), true);

        return mapToResponse(appointmentEntity);
    }

    public List<AppointmentResponse> getMyAppointments(Principal principal) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        if (user.getRole().name().equals("DOCTOR")) {
            DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
            return appointmentRepository.findAllByDoctor_Id(doctor.getId())
                    .stream()
                    .filter(appointment -> appointment.getDoctor() != null && appointment.getPatient() != null)
                    .map(this::mapToResponse)
                    .toList();
        } else if (user.getRole().name().equals("PATIENT")) {
            PatientEntity patient = patientRepository.findByUser_Id(user.getId());
            return appointmentRepository.findAllByPatient_Id(patient.getId())
                    .stream()
                    .filter(appointment -> appointment.getDoctor() != null && appointment.getPatient() != null)
                    .map(this::mapToResponse)
                    .toList();
        } else {
            throw new RuntimeException("Faqat Doctor yoki Patient appointmentlarga ega bo‘ladi");
        }
    }

    public String handleAppointmentAction(Principal principal, Long appointmentId, boolean approve) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);

        if (approve) {
            if (!user.getRole().name().equalsIgnoreCase("DOCTOR")) {
                throw new RuntimeException("Faqat shifokorlar tasdiqlashi mumkin");
            }
            approveAppointment(principal, appointmentId);
            return "✅ Appointment tasdiqlandi";
        } else {
            handleCancellation(principal, appointmentId);
            return "❌ Appointment bekor qilindi";
        }
    }

    public void approveAppointment(Principal principal, Long appointmentId) {
        UserEntity doctorUser = authHelperService.getUserFromPrincipal(principal);

        AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment topilmadi"));

        if (!appointment.getDoctor().getUser().getId().equals(doctorUser.getId())) {
            throw new RuntimeException("Boshqa shifokorning appointmentini tasdiqlay olmaysiz");
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new RuntimeException("Faqat PENDING statusdagi appointment tasdiqlanishi mumkin");
        }

        appointment.setStatus(AppointmentStatus.APPROVED);
        appointmentRepository.save(appointment);

        String message = "✅ Appointment tasdiqlandi: " +
                appointment.getDate() + " " + appointment.getTime();

        messageService.sendMessage(principal, appointment.getChat().getId(), new MessageRequest(message), true);
    }

    public void handleCancellation(Principal principal, Long appointmentId) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);

        AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment topilmadi"));

        Long userId = user.getId();
        Long doctorUserId = appointment.getDoctor().getUser().getId();
        Long patientUserId = appointment.getPatient().getUser().getId();

        boolean isPatient = userId.equals(patientUserId);
        boolean isDoctor = userId.equals(doctorUserId);

        if (!isPatient && !isDoctor) {
            throw new RuntimeException("Siz bu appointmentni bekor qila olmaysiz");
        }

        if (appointment.getStatus() == AppointmentStatus.REJECTED) {
            throw new RuntimeException("Appointment allaqachon bekor qilingan");
        }

        appointment.setStatus(AppointmentStatus.REJECTED);
        appointmentRepository.save(appointment);

        String cancelMessage = isPatient
                ? "🛑 Bemordan appointmentni bekor qilish so‘rovi."
                : "🛑 Appointment shifokor tomonidan bekor qilindi.";

        messageService.sendMessage(principal, appointment.getChat().getId(), new MessageRequest(cancelMessage), true);
    }

    private String buildAppointmentMessage(AppointmentEntity appointment) {
        return "✅ Yangi appointment:\n" +
                "📍 Branch: " + appointment.getBranch().getBranchName() + "\n" +
                "🕒 Sana: " + appointment.getDate() + " " + appointment.getTime() + "\n" +
                (appointment.getDisease() != null
                        ? "🩺 Xizmat: " + appointment.getDisease().getDiseaseName()
                        : "📝 Sabab: " + appointment.getCustomReason());
    }

    private AppointmentResponse mapToResponse(AppointmentEntity appointmentEntity) {
        return AppointmentResponse.builder()
                .id(appointmentEntity.getId())
                .doctorId(appointmentEntity.getDoctor().getId())
                .doctorName(appointmentEntity.getDoctor().getFirstname())
                .patientId(appointmentEntity.getPatient().getId())
                .patientName(appointmentEntity.getPatient().getFirstname())
                .branchId(appointmentEntity.getBranch().getId())
                .branchName(appointmentEntity.getBranch().getBranchName())
                .diseaseId(appointmentEntity.getDisease().getId())
                .diseaseName(appointmentEntity.getDisease().getDiseaseName())
                .date(appointmentEntity.getDate())
                .time(appointmentEntity.getTime())
                .customReason(appointmentEntity.getCustomReason())
                .status(appointmentEntity.getStatus())
                .build();
    }
}
