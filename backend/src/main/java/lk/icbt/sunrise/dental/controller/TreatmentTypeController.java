package lk.icbt.sunrise.dental.controller;

import lk.icbt.sunrise.dental.dto.TreatmentTypeResponse;
import lk.icbt.sunrise.dental.repository.TreatmentTypeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/treatment-types")
public class TreatmentTypeController {

    private final TreatmentTypeRepository treatmentTypeRepository;

    public TreatmentTypeController(TreatmentTypeRepository treatmentTypeRepository) {
        this.treatmentTypeRepository = treatmentTypeRepository;
    }

    @GetMapping
    public List<TreatmentTypeResponse> listAll() {
        return treatmentTypeRepository.findAll().stream().map(TreatmentTypeResponse::from).toList();
    }
}
