package lk.icbt.sunrise.dental.service;

import lk.icbt.sunrise.dental.domain.*;
import lk.icbt.sunrise.dental.repository.BillRepository;
import lk.icbt.sunrise.dental.service.billing.BillingStrategy;
import lk.icbt.sunrise.dental.service.billing.BillingStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock
    private BillRepository billRepository;
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private BillingStrategyFactory billingStrategyFactory;
    @Mock
    private BillingStrategy billingStrategy;

    private BillService billService;

    private Appointment buildAppointment() {
        TreatmentType treatmentType = new TreatmentType("Tooth Extraction", new BigDecimal("5000.00"), new BigDecimal("1500.00"));
        Patient patient = new Patient("Kasun Silva", "Colombo", "0711112222");
        Dentist dentist = new Dentist("Dr. Nimal Perera", "General Dentistry", "0771234567");
        return Appointment.builder()
                .appointmentNumber("APT-000001")
                .patient(patient)
                .dentist(dentist)
                .treatmentType(treatmentType)
                .appointmentDate(LocalDate.now())
                .appointmentTime(LocalTime.of(9, 0))
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }

    @BeforeEach
    void setUp() {
        billService = new BillService(billRepository, appointmentService, billingStrategyFactory);
    }

    @Test
    void generateBill_createsNewBill_whenNoneExistsYet() {
        Appointment appointment = buildAppointment();
        when(appointmentService.findByAppointmentNumber("APT-000001")).thenReturn(appointment);
        when(billRepository.findByAppointment_IdWithDetails(null)).thenReturn(Optional.empty());
        when(billingStrategyFactory.resolve(appointment.getTreatmentType())).thenReturn(billingStrategy);
        when(billingStrategy.calculateTreatmentCost(appointment.getTreatmentType())).thenReturn(new BigDecimal("5500.00"));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bill bill = billService.generateBill("APT-000001");

        assertEquals(new BigDecimal("1500.00"), bill.getConsultationFee());
        assertEquals(new BigDecimal("5500.00"), bill.getTreatmentCost());
        assertEquals(new BigDecimal("7000.00"), bill.getTotalAmount());
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
    }

    @Test
    void generateBill_returnsExistingBill_whenAlreadyGenerated() {
        Appointment appointment = buildAppointment();
        Bill existingBill = new Bill(appointment, new BigDecimal("1500.00"), new BigDecimal("5500.00"), new BigDecimal("7000.00"));
        when(appointmentService.findByAppointmentNumber("APT-000001")).thenReturn(appointment);
        when(billRepository.findByAppointment_IdWithDetails(null)).thenReturn(Optional.of(existingBill));

        Bill bill = billService.generateBill("APT-000001");

        assertSame(existingBill, bill);
        verify(billingStrategyFactory, never()).resolve(any());
        verify(billRepository, never()).save(any());
    }
}
