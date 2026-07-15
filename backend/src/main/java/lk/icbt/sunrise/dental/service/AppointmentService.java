package lk.icbt.sunrise.dental.service;

import jakarta.annotation.PostConstruct;
import lk.icbt.sunrise.dental.domain.*;
import lk.icbt.sunrise.dental.dto.AppointmentRegistrationRequest;
import lk.icbt.sunrise.dental.exception.DoubleBookingException;
import lk.icbt.sunrise.dental.exception.InvalidAppointmentTimeException;
import lk.icbt.sunrise.dental.exception.ResourceNotFoundException;
import lk.icbt.sunrise.dental.repository.AppointmentRepository;
import lk.icbt.sunrise.dental.repository.DentistRepository;
import lk.icbt.sunrise.dental.repository.PatientRepository;
import lk.icbt.sunrise.dental.repository.TreatmentTypeRepository;
import lk.icbt.sunrise.dental.service.notification.AppointmentEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    private static final LocalTime CLINIC_OPENS = LocalTime.of(9, 0);
    private static final LocalTime CLINIC_CLOSES = LocalTime.of(17, 0);

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentTypeRepository treatmentTypeRepository;
    private final AppointmentEventPublisher eventPublisher;

    public AppointmentService(AppointmentRepository appointmentRepository,
                               PatientRepository patientRepository,
                               DentistRepository dentistRepository,
                               TreatmentTypeRepository treatmentTypeRepository,
                               AppointmentEventPublisher eventPublisher) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.treatmentTypeRepository = treatmentTypeRepository;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    void initAppointmentNumberSequence() {
        AppointmentNumberGenerator.initialize(appointmentRepository.count());
    }

    @Transactional
    public Appointment register(AppointmentRegistrationRequest request) {
        if (request.appointmentTime().isBefore(CLINIC_OPENS) || request.appointmentTime().isAfter(CLINIC_CLOSES)) {
            throw new InvalidAppointmentTimeException("Appointment time must be between "
                    + CLINIC_OPENS + " and " + CLINIC_CLOSES);
        }

        Dentist dentist = dentistRepository.findById(request.dentistId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found: " + request.dentistId()));
        TreatmentType treatmentType = treatmentTypeRepository.findById(request.treatmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Treatment type not found: " + request.treatmentTypeId()));

        boolean slotTaken = appointmentRepository.existsByDentist_IdAndAppointmentDateAndAppointmentTime(
                dentist.getId(), request.appointmentDate(), request.appointmentTime());
        if (slotTaken) {
            throw new DoubleBookingException("Dr. " + dentist.getName() + " already has an appointment at "
                    + request.appointmentDate() + " " + request.appointmentTime());
        }

        Patient patient = resolvePatient(request);

        Appointment appointment = Appointment.builder()
                .appointmentNumber(AppointmentNumberGenerator.getInstance().next())
                .patient(patient)
                .dentist(dentist)
                .treatmentType(treatmentType)
                .appointmentDate(request.appointmentDate())
                .appointmentTime(request.appointmentTime())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        eventPublisher.publishAppointmentCreated(saved);
        return saved;
    }

    private Patient resolvePatient(AppointmentRegistrationRequest request) {
        return patientRepository.findByContactNumber(request.contactNumber()).stream()
                .filter(existing -> existing.getName().equalsIgnoreCase(request.patientName()))
                .findFirst()
                .orElseGet(() -> patientRepository.save(
                        new Patient(request.patientName(), request.address(), request.contactNumber())));
    }

    @Transactional(readOnly = true)
    public Appointment findByAppointmentNumber(String appointmentNumber) {
        return appointmentRepository.findByAppointmentNumberWithDetails(appointmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + appointmentNumber));
    }

    @Transactional(readOnly = true)
    public List<Appointment> findByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDateWithDetails(date);
    }
}
