package lk.icbt.sunrise.dental.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_number", nullable = false, unique = true, length = 20)
    private String appointmentNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dentist_id", nullable = false)
    private Dentist dentist;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treatment_type_id", nullable = false)
    private TreatmentType treatmentType;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Appointment() {
    }

    private Appointment(Builder builder) {
        this.appointmentNumber = builder.appointmentNumber;
        this.patient = builder.patient;
        this.dentist = builder.dentist;
        this.treatmentType = builder.treatmentType;
        this.appointmentDate = builder.appointmentDate;
        this.appointmentTime = builder.appointmentTime;
        this.status = builder.status;
    }

    public Long getId() {
        return id;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public Patient getPatient() {
        return patient;
    }

    public Dentist getDentist() {
        return dentist;
    }

    public TreatmentType getTreatmentType() {
        return treatmentType;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Builder pattern: an Appointment has several mandatory associations (patient, dentist,
     * treatment type, date/time) plus a generated appointment number, so construction is
     * assembled step by step rather than via a wide constructor.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String appointmentNumber;
        private Patient patient;
        private Dentist dentist;
        private TreatmentType treatmentType;
        private LocalDate appointmentDate;
        private LocalTime appointmentTime;
        private AppointmentStatus status = AppointmentStatus.SCHEDULED;

        public Builder appointmentNumber(String appointmentNumber) {
            this.appointmentNumber = appointmentNumber;
            return this;
        }

        public Builder patient(Patient patient) {
            this.patient = patient;
            return this;
        }

        public Builder dentist(Dentist dentist) {
            this.dentist = dentist;
            return this;
        }

        public Builder treatmentType(TreatmentType treatmentType) {
            this.treatmentType = treatmentType;
            return this;
        }

        public Builder appointmentDate(LocalDate appointmentDate) {
            this.appointmentDate = appointmentDate;
            return this;
        }

        public Builder appointmentTime(LocalTime appointmentTime) {
            this.appointmentTime = appointmentTime;
            return this;
        }

        public Builder status(AppointmentStatus status) {
            this.status = status;
            return this;
        }

        public Appointment build() {
            if (appointmentNumber == null || patient == null || dentist == null
                    || treatmentType == null || appointmentDate == null || appointmentTime == null) {
                throw new IllegalStateException("Appointment is missing required fields");
            }
            return new Appointment(this);
        }
    }
}
