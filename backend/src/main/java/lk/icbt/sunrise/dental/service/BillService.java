package lk.icbt.sunrise.dental.service;

import lk.icbt.sunrise.dental.domain.Appointment;
import lk.icbt.sunrise.dental.domain.AppointmentStatus;
import lk.icbt.sunrise.dental.domain.Bill;
import lk.icbt.sunrise.dental.domain.TreatmentType;
import lk.icbt.sunrise.dental.repository.BillRepository;
import lk.icbt.sunrise.dental.service.billing.BillingStrategy;
import lk.icbt.sunrise.dental.service.billing.BillingStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final AppointmentService appointmentService;
    private final BillingStrategyFactory billingStrategyFactory;

    public BillService(BillRepository billRepository,
                        AppointmentService appointmentService,
                        BillingStrategyFactory billingStrategyFactory) {
        this.billRepository = billRepository;
        this.appointmentService = appointmentService;
        this.billingStrategyFactory = billingStrategyFactory;
    }

    @Transactional
    public Bill generateBill(String appointmentNumber) {
        Appointment appointment = appointmentService.findByAppointmentNumber(appointmentNumber);

        return billRepository.findByAppointment_IdWithDetails(appointment.getId())
                .orElseGet(() -> createBill(appointment));
    }

    private Bill createBill(Appointment appointment) {
        TreatmentType treatmentType = appointment.getTreatmentType();
        BillingStrategy strategy = billingStrategyFactory.resolve(treatmentType);

        BigDecimal treatmentCost = strategy.calculateTreatmentCost(treatmentType);
        BigDecimal consultationFee = treatmentType.getConsultationFee();
        BigDecimal totalAmount = treatmentCost.add(consultationFee);

        Bill bill = new Bill(appointment, consultationFee, treatmentCost, totalAmount);
        Bill saved = billRepository.save(bill);

        appointment.setStatus(AppointmentStatus.COMPLETED);
        return saved;
    }
}
