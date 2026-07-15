package lk.icbt.sunrise.dental.controller;

import jakarta.validation.Valid;
import lk.icbt.sunrise.dental.domain.Appointment;
import lk.icbt.sunrise.dental.dto.AppointmentRegistrationRequest;
import lk.icbt.sunrise.dental.dto.AppointmentResponse;
import lk.icbt.sunrise.dental.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse register(@Valid @RequestBody AppointmentRegistrationRequest request) {
        Appointment appointment = appointmentService.register(request);
        return AppointmentResponse.from(appointment);
    }

    @GetMapping("/{appointmentNumber}")
    public AppointmentResponse getByNumber(@PathVariable String appointmentNumber) {
        return AppointmentResponse.from(appointmentService.findByAppointmentNumber(appointmentNumber));
    }
}
