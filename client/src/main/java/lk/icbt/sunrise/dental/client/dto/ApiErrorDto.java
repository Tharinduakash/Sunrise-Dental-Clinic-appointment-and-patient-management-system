package lk.icbt.sunrise.dental.client.dto;

import java.util.List;

public record ApiErrorDto(String timestamp, int status, String error, String message, List<String> details) {
}
