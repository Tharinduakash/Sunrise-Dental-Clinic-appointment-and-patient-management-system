package lk.icbt.sunrise.dental.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "treatment_types")
public class TreatmentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "base_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseCost;

    @Column(name = "consultation_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee;

    protected TreatmentType() {
    }

    public TreatmentType(String name, BigDecimal baseCost, BigDecimal consultationFee) {
        this.name = name;
        this.baseCost = baseCost;
        this.consultationFee = consultationFee;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBaseCost() {
        return baseCost;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }
}
