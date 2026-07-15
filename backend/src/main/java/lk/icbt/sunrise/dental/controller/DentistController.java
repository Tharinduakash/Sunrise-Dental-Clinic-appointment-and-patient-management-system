package lk.icbt.sunrise.dental.controller;

import lk.icbt.sunrise.dental.dto.DentistResponse;
import lk.icbt.sunrise.dental.repository.DentistRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dentists")
public class DentistController {

    private final DentistRepository dentistRepository;

    public DentistController(DentistRepository dentistRepository) {
        this.dentistRepository = dentistRepository;
    }

    @GetMapping
    public List<DentistResponse> listAll() {
        return dentistRepository.findAll().stream().map(DentistResponse::from).toList();
    }
}
