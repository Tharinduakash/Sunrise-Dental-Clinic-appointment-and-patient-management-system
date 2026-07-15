package lk.icbt.sunrise.dental.service;

import lk.icbt.sunrise.dental.domain.Appointment;
import lk.icbt.sunrise.dental.dto.AppointmentResponse;
import lk.icbt.sunrise.dental.dto.DailyReportResponse;
import lk.icbt.sunrise.dental.service.billing.BillingStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Extra value-add feature (beyond the six mandatory functions): a decision-support
 * report summarising a day's schedule and its expected revenue, so clinic
 * management can see workload and income at a glance instead of counting paper slips.
 */
@Service
public class ReportService {

    private final AppointmentService appointmentService;
    private final BillingStrategyFactory billingStrategyFactory;

    public ReportService(AppointmentService appointmentService, BillingStrategyFactory billingStrategyFactory) {
        this.appointmentService = appointmentService;
        this.billingStrategyFactory = billingStrategyFactory;
    }

    @Transactional(readOnly = true)
    public DailyReportResponse dailyReport(LocalDate date) {
        List<Appointment> appointments = appointmentService.findByDate(date);

        // Reuses the same BillingStrategy the billing service applies, so this projection
        // matches what each appointment will actually be billed for (e.g. the surgical
        // surcharge), rather than a separate estimate that could silently drift out of sync.
        BigDecimal totalRevenue = appointments.stream()
                .map(a -> billingStrategyFactory.resolve(a.getTreatmentType()).calculateTreatmentCost(a.getTreatmentType())
                        .add(a.getTreatmentType().getConsultationFee()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<AppointmentResponse> appointmentResponses = appointments.stream()
                .map(AppointmentResponse::from)
                .toList();

        return new DailyReportResponse(date, appointments.size(), totalRevenue, appointmentResponses);
    }
}
