package lk.icbt.sunrise.dental.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DailyReportResponse(
        LocalDate date,
        int totalAppointments,
        BigDecimal totalRevenue,
        List<AppointmentResponse> appointments
) {
}
