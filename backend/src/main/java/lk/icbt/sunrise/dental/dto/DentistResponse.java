package lk.icbt.sunrise.dental.dto;

import lk.icbt.sunrise.dental.domain.Dentist;

public record DentistResponse(Long id, String name, String specialization) {
    public static DentistResponse from(Dentist dentist) {
        return new DentistResponse(dentist.getId(), dentist.getName(), dentist.getSpecialization());
    }
}
