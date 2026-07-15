package lk.icbt.sunrise.dental.client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DailyReportDto(
        LocalDate date,
        int totalAppointments,
        BigDecimal totalRevenue,
        List<AppointmentDto> appointments
) {
}
