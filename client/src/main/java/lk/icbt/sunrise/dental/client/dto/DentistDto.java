package lk.icbt.sunrise.dental.client.dto;

public record DentistDto(Long id, String name, String specialization) {
    @Override
    public String toString() {
        return name + " (" + specialization + ")";
    }
}
