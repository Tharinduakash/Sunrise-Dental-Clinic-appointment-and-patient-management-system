package lk.icbt.sunrise.dental.service;

import lk.icbt.sunrise.dental.domain.*;
import lk.icbt.sunrise.dental.dto.DailyReportResponse;
import lk.icbt.sunrise.dental.service.billing.BillingStrategy;
import lk.icbt.sunrise.dental.service.billing.BillingStrategyFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Regression test for a real bug found during manual end-to-end verification: the daily
 * report originally summed treatmentType.baseCost directly, ignoring any BillingStrategy
 * surcharge, so its "estimated revenue" silently disagreed with what BillService would
 * actually bill. This test locks in the fix (both must use the same BillingStrategyFactory).
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private AppointmentService appointmentService;
    @Mock
    private BillingStrategyFactory billingStrategyFactory;
    @Mock
    private BillingStrategy surgicalStrategy;

    @Test
    void dailyReport_usesBillingStrategyCost_notRawBaseCost() {
        TreatmentType extraction = new TreatmentType("Tooth Extraction", new BigDecimal("5000.00"), new BigDecimal("1500.00"));
        Patient patient = new Patient("Kasun Silva", "Colombo", "0711112222");
        Dentist dentist = new Dentist("Dr. Ruwan Jayasuriya", "Oral Surgery", "0712345678");
        Appointment appointment = Appointment.builder()
                .appointmentNumber("APT-000001")
                .patient(patient)
                .dentist(dentist)
                .treatmentType(extraction)
                .appointmentDate(LocalDate.of(2026, 7, 20))
                .appointmentTime(LocalTime.of(10, 30))
                .status(AppointmentStatus.SCHEDULED)
                .build();

        when(appointmentService.findByDate(LocalDate.of(2026, 7, 20))).thenReturn(List.of(appointment));
        when(billingStrategyFactory.resolve(extraction)).thenReturn(surgicalStrategy);
        // Strategy applies the 10% surgical surcharge: 5000.00 -> 5500.00 (not the raw baseCost)
        when(surgicalStrategy.calculateTreatmentCost(extraction)).thenReturn(new BigDecimal("5500.00"));

        ReportService reportService = new ReportService(appointmentService, billingStrategyFactory);
        DailyReportResponse report = reportService.dailyReport(LocalDate.of(2026, 7, 20));

        // 5500.00 (strategy-calculated treatment cost) + 1500.00 (consultation fee) = 7000.00
        // A regression to the old bug would produce 6500.00 (raw baseCost + fee) instead.
        assertEquals(new BigDecimal("7000.00"), report.totalRevenue());
        assertEquals(1, report.totalAppointments());
    }
}
