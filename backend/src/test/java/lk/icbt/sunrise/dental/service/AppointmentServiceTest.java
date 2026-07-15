package lk.icbt.sunrise.dental.service;

import lk.icbt.sunrise.dental.domain.Appointment;
import lk.icbt.sunrise.dental.domain.Dentist;
import lk.icbt.sunrise.dental.domain.Patient;
import lk.icbt.sunrise.dental.domain.TreatmentType;
import lk.icbt.sunrise.dental.dto.AppointmentRegistrationRequest;
import lk.icbt.sunrise.dental.exception.DoubleBookingException;
import lk.icbt.sunrise.dental.exception.InvalidAppointmentTimeException;
import lk.icbt.sunrise.dental.exception.ResourceNotFoundException;
import lk.icbt.sunrise.dental.repository.AppointmentRepository;
import lk.icbt.sunrise.dental.repository.DentistRepository;
import lk.icbt.sunrise.dental.repository.PatientRepository;
import lk.icbt.sunrise.dental.repository.TreatmentTypeRepository;
import lk.icbt.sunrise.dental.service.notification.AppointmentEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DentistRepository dentistRepository;
    @Mock
    private TreatmentTypeRepository treatmentTypeRepository;
    @Mock
    private AppointmentEventPublisher eventPublisher;

    private AppointmentService appointmentService;

    private final Dentist dentist = new Dentist("Dr. Nimal Perera", "General Dentistry", "0771234567");
    private final TreatmentType treatmentType = new TreatmentType("General Checkup", BigDecimal.ZERO, new BigDecimal("1500.00"));

    private AppointmentRegistrationRequest validRequest() {
        return new AppointmentRegistrationRequest(
                "Kasun Silva", "123 Galle Road, Colombo", "0711112222",
                1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(10, 0));
    }

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService(
                appointmentRepository, patientRepository, dentistRepository, treatmentTypeRepository, eventPublisher);
        appointmentService.initAppointmentNumberSequence();
    }

    @Test
    void register_savesNewAppointment_whenSlotIsFree() {
        AppointmentRegistrationRequest request = validRequest();
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentTypeRepository.findById(1L)).thenReturn(Optional.of(treatmentType));
        when(appointmentRepository.existsByDentist_IdAndAppointmentDateAndAppointmentTime(any(), any(), any()))
                .thenReturn(false);
        when(patientRepository.findByContactNumber(request.contactNumber())).thenReturn(List.of());
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment result = appointmentService.register(request);

        assertNotNull(result.getAppointmentNumber());
        assertEquals("Kasun Silva", result.getPatient().getName());
        verify(eventPublisher).publishAppointmentCreated(result);
    }

    @Test
    void register_throwsDoubleBookingException_whenDentistSlotAlreadyTaken() {
        AppointmentRegistrationRequest request = validRequest();
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentTypeRepository.findById(1L)).thenReturn(Optional.of(treatmentType));
        when(appointmentRepository.existsByDentist_IdAndAppointmentDateAndAppointmentTime(any(), any(), any()))
                .thenReturn(true);

        assertThrows(DoubleBookingException.class, () -> appointmentService.register(request));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void register_throwsResourceNotFoundException_whenTreatmentTypeMissing() {
        AppointmentRegistrationRequest request = validRequest();
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.register(request));
    }

    @Test
    void register_reusesExistingPatient_whenNameAndContactMatch() {
        AppointmentRegistrationRequest request = validRequest();
        Patient existingPatient = new Patient("Kasun Silva", "Old Address", request.contactNumber());
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentTypeRepository.findById(1L)).thenReturn(Optional.of(treatmentType));
        when(appointmentRepository.existsByDentist_IdAndAppointmentDateAndAppointmentTime(any(), any(), any()))
                .thenReturn(false);
        when(patientRepository.findByContactNumber(request.contactNumber())).thenReturn(List.of(existingPatient));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment result = appointmentService.register(request);

        assertSame(existingPatient, result.getPatient());
        verify(patientRepository, never()).save(any());
    }

    // --- Display Appointment Details (findByAppointmentNumber) ---
    // Added after a traceability review showed this mandatory function had no direct
    // unit test - only indirect mock coverage via BillServiceTest.

    @Test
    void findByAppointmentNumber_returnsAppointment_whenFound() {
        Appointment appointment = Appointment.builder()
                .appointmentNumber("APT-000001")
                .patient(new Patient("Kasun Silva", "Colombo", "0711112222"))
                .dentist(dentist)
                .treatmentType(treatmentType)
                .appointmentDate(LocalDate.now().plusDays(1))
                .appointmentTime(LocalTime.of(10, 0))
                .build();
        when(appointmentRepository.findByAppointmentNumberWithDetails("APT-000001"))
                .thenReturn(Optional.of(appointment));

        Appointment result = appointmentService.findByAppointmentNumber("APT-000001");

        assertEquals("APT-000001", result.getAppointmentNumber());
    }

    @Test
    void findByAppointmentNumber_throwsResourceNotFoundException_whenNoMatch() {
        when(appointmentRepository.findByAppointmentNumberWithDetails("APT-999999"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.findByAppointmentNumber("APT-999999"));
    }

    // --- Clinic operating hours (09:00-17:00) validation ---
    // Written before the corresponding production code existed (see docs/test-plan.md,
    // TDD narrative): this is a red-green-refactor addition, not a retrofit.

    @Test
    void register_throwsInvalidAppointmentTimeException_whenTimeBeforeOpeningHours() {
        AppointmentRegistrationRequest request = new AppointmentRegistrationRequest(
                "Kasun Silva", "123 Galle Road, Colombo", "0711112222",
                1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(8, 30));

        assertThrows(InvalidAppointmentTimeException.class, () -> appointmentService.register(request));
        // appointmentRepository.count() is expected here (called once, from @BeforeEach's
        // initAppointmentNumberSequence) - the time guard must still stop everything past that.
        verifyNoInteractions(dentistRepository, treatmentTypeRepository);
        verify(appointmentRepository, never()).existsByDentist_IdAndAppointmentDateAndAppointmentTime(any(), any(), any());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void register_throwsInvalidAppointmentTimeException_whenTimeAfterClosingHours() {
        AppointmentRegistrationRequest request = new AppointmentRegistrationRequest(
                "Kasun Silva", "123 Galle Road, Colombo", "0711112222",
                1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(17, 30));

        assertThrows(InvalidAppointmentTimeException.class, () -> appointmentService.register(request));
        // appointmentRepository.count() is expected here (called once, from @BeforeEach's
        // initAppointmentNumberSequence) - the time guard must still stop everything past that.
        verifyNoInteractions(dentistRepository, treatmentTypeRepository);
        verify(appointmentRepository, never()).existsByDentist_IdAndAppointmentDateAndAppointmentTime(any(), any(), any());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void register_succeeds_whenTimeAtOpeningBoundary() {
        AppointmentRegistrationRequest request = new AppointmentRegistrationRequest(
                "Kasun Silva", "123 Galle Road, Colombo", "0711112222",
                1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(9, 0));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentTypeRepository.findById(1L)).thenReturn(Optional.of(treatmentType));
        when(appointmentRepository.existsByDentist_IdAndAppointmentDateAndAppointmentTime(any(), any(), any()))
                .thenReturn(false);
        when(patientRepository.findByContactNumber(request.contactNumber())).thenReturn(List.of());
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> appointmentService.register(request));
    }

    @Test
    void register_succeeds_whenTimeAtClosingBoundary() {
        AppointmentRegistrationRequest request = new AppointmentRegistrationRequest(
                "Kasun Silva", "123 Galle Road, Colombo", "0711112222",
                1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(17, 0));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentTypeRepository.findById(1L)).thenReturn(Optional.of(treatmentType));
        when(appointmentRepository.existsByDentist_IdAndAppointmentDateAndAppointmentTime(any(), any(), any()))
                .thenReturn(false);
        when(patientRepository.findByContactNumber(request.contactNumber())).thenReturn(List.of());
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> appointmentService.register(request));
    }
}
