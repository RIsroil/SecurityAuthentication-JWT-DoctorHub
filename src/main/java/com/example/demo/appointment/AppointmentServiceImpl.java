package com.example.demo.appointment;

import com.example.demo.appointment.mapper.AppointmentMapper;
import com.example.demo.appointment.model.AppointmentRequest;
import com.example.demo.appointment.model.AppointmentView;
import com.example.demo.branch.BranchEntity;
import com.example.demo.branch.BranchRepository;
import com.example.demo.chat.ChatEntity;
import com.example.demo.chat.ChatRepository;
import com.example.demo.chat.ChatService;
import com.example.demo.disease.DiseaseEntity;
import com.example.demo.disease.DiseaseRepository;
import com.example.demo.doctor.DoctorEntity;
import com.example.demo.doctor.DoctorRepository;
import com.example.demo.exception.InvalidInputException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.message.MessageService;
import com.example.demo.message.model.MessageRequest;
import com.example.demo.patient.PatientEntity;
import com.example.demo.patient.PatientRepository;
import com.example.demo.user.UserEntity;
import com.example.demo.user.Role;
import com.example.demo.user.auth.AuthHelperService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final BranchRepository branchRepository;
    private final DiseaseRepository diseaseRepository;
    private final ChatService chatService;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final AuthHelperService authHelperService;
    private final MessageService messageService;
    private final ChatRepository chatRepository;
    private final AppointmentMapper appointmentMapper = AppointmentMapper.INSTANCE;

    @Override
    @Transactional
    public AppointmentView create(Principal principal, Long doctorId, AppointmentRequest request) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        if (user.getRole() != Role.PATIENT) {
            throw new InvalidInputException("Only patients can create appointments.");
        }

        PatientEntity patient = patientRepository.findByUser_Id(user.getId());
        if (patient == null) {
            throw new ResourceNotFoundException("Patient record not found for user: " + user.getId());
        }
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));

        BranchEntity branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + request.getBranchId()));

        DiseaseEntity diseaseEntity = null;
        String customReason = request.getCustomReason();
        Long diseaseId = request.getDiseaseId();

        if (diseaseId != null && customReason != null && !customReason.isBlank()) {
            throw new InvalidInputException("Provide either diseaseId or customReason, not both.");
        }
        if ((diseaseId == null) && (customReason == null || customReason.isBlank())) {
            throw new InvalidInputException("Either diseaseId or customReason must be provided.");
        }
        if (diseaseId != null) {
            diseaseEntity = diseaseRepository.findById(diseaseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Disease not found with ID: " + diseaseId));
        }

        ChatEntity chat = chatService.createOrGetChat(principal, doctorId);
        // chat = chatRepository.save(chat); // chatService should handle saving if it creates

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

        AppointmentEntity savedAppointment = appointmentRepository.save(appointmentEntity);

        String messageText = buildAppointmentMessage(savedAppointment);
        // Assuming messageService.sendMessage takes care of identifying the sender from principal for the message entity
        messageService.sendMessage(principal, chat.getId(), new MessageRequest(messageText), true);


        return appointmentMapper.toView(savedAppointment);
    }

    @Override
    public List<AppointmentView> getMyAppointments(Principal principal) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        List<AppointmentEntity> appointments;

        if (user.getRole() == Role.DOCTOR) {
            DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
            if (doctor == null) throw new ResourceNotFoundException("Doctor record not found for user: " + user.getId());
            appointments = appointmentRepository.findAllByDoctor_Id(doctor.getId());
        } else if (user.getRole() == Role.PATIENT) {
            PatientEntity patient = patientRepository.findByUser_Id(user.getId());
            if (patient == null) throw new ResourceNotFoundException("Patient record not found for user: " + user.getId());
            appointments = appointmentRepository.findAllByPatient_Id(patient.getId());
        } else {
            throw new InvalidInputException("User role not supported for fetching appointments.");
        }

        return appointments.stream()
                .filter(appointment -> appointment.getDoctor() != null && appointment.getPatient() != null && appointment.getChat() != null) // Ensure essential linked entities are present
                .map(appointmentMapper::toView)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String handleAppointmentAction(Principal principal, Long appointmentId, boolean approveAction) {
        UserEntity currentUser = authHelperService.getUserFromPrincipal(principal);
        AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        if (approveAction) { // Approve action
            if (currentUser.getRole() != Role.DOCTOR) {
                throw new InvalidInputException("Only doctors can approve appointments.");
            }
            if (!appointment.getDoctor().getUser().getId().equals(currentUser.getId())) {
                throw new InvalidInputException("You can only approve your own appointments.");
            }
            if (appointment.getStatus() != AppointmentStatus.PENDING) {
                throw new InvalidInputException("Only PENDING appointments can be approved. Current status: " + appointment.getStatus());
            }
            appointment.setStatus(AppointmentStatus.APPROVED);
            appointmentRepository.save(appointment);
            String message = "✅ Appointment approved for " + appointment.getDate() + " " + appointment.getTime();
            messageService.sendMessage(principal, appointment.getChat().getId(), new MessageRequest(message), true);
            return "✅ Appointment approved successfully.";
        } else { // Reject/Cancel action
            Long currentUserId = currentUser.getId();
            Long doctorUserId = appointment.getDoctor().getUser().getId();
            Long patientUserId = appointment.getPatient().getUser().getId();

            boolean isCurrentUserPatient = currentUserId.equals(patientUserId) && currentUser.getRole() == Role.PATIENT;
            boolean isCurrentUserDoctor = currentUserId.equals(doctorUserId) && currentUser.getRole() == Role.DOCTOR;

            if (!isCurrentUserPatient && !isCurrentUserDoctor) {
                throw new InvalidInputException("You are not authorized to cancel/reject this appointment.");
            }
            if (appointment.getStatus() == AppointmentStatus.REJECTED || appointment.getStatus() == AppointmentStatus.CANCELLED) { // Assuming CANCELLED is also a final state
                throw new InvalidInputException("Appointment has already been cancelled/rejected.");
            }

            appointment.setStatus(AppointmentStatus.REJECTED); // Or CANCELLED, based on who performs it. Let's use REJECTED for now.
            appointmentRepository.save(appointment);

            String cancelMessage = isCurrentUserPatient
                    ? "🛑 Patient cancelled the appointment."
                    : "🛑 Doctor rejected/cancelled the appointment.";
            messageService.sendMessage(principal, appointment.getChat().getId(), new MessageRequest(cancelMessage), true);
            return "❌ Appointment rejected/cancelled successfully.";
        }
    }

    private String buildAppointmentMessage(AppointmentEntity appointment) {
        return "✅ New appointment request:\n" +
                "📍 Branch: " + appointment.getBranch().getBranchName() + "\n" +
                "🕒 Date: " + appointment.getDate() + " " + appointment.getTime() + "\n" +
                (appointment.getDisease() != null
                        ? "🩺 Service: " + appointment.getDisease().getDiseaseName()
                        : "📝 Reason: " + appointment.getCustomReason());
    }
}
