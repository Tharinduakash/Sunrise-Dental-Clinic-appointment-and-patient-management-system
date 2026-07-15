package lk.icbt.sunrise.dental.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lk.icbt.sunrise.dental.client.dto.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

/**
 * Thin REST client for the dental-backend web service. Holds the bearer token for the
 * current staff session in memory only (never persisted to disk), mirroring how a browser
 * session would behave, and attaches it to every request after login.
 */
public class ApiClient {

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String sessionToken;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public boolean isLoggedIn() {
        return sessionToken != null;
    }

    public void logout() {
        sessionToken = null;
    }

    public LoginResponseDto login(String username, String password) throws ApiException {
        LoginRequestDto body = new LoginRequestDto(username, password);
        LoginResponseDto response = send(post("/api/auth/login", body, false), LoginResponseDto.class);
        this.sessionToken = response.token();
        return response;
    }

    public List<DentistDto> listDentists() throws ApiException {
        return send(get("/api/dentists"), objectMapper.getTypeFactory().constructCollectionType(List.class, DentistDto.class));
    }

    public List<TreatmentTypeDto> listTreatmentTypes() throws ApiException {
        return send(get("/api/treatment-types"), objectMapper.getTypeFactory().constructCollectionType(List.class, TreatmentTypeDto.class));
    }

    public AppointmentDto registerAppointment(AppointmentRegistrationRequestDto request) throws ApiException {
        return send(post("/api/appointments", request, true), AppointmentDto.class);
    }

    public AppointmentDto getAppointment(String appointmentNumber) throws ApiException {
        return send(get("/api/appointments/" + appointmentNumber), AppointmentDto.class);
    }

    public BillDto generateBill(String appointmentNumber) throws ApiException {
        return send(get("/api/bills/" + appointmentNumber), BillDto.class);
    }

    public DailyReportDto dailyReport(LocalDate date) throws ApiException {
        return send(get("/api/reports/daily?date=" + date), DailyReportDto.class);
    }

    private HttpRequest.Builder baseRequest(String path, boolean requiresAuth) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
        if (requiresAuth && sessionToken != null) {
            builder.header("Authorization", "Bearer " + sessionToken);
        }
        return builder;
    }

    private HttpRequest get(String path) {
        return baseRequest(path, true).GET().build();
    }

    private HttpRequest post(String path, Object body, boolean requiresAuth) throws ApiException {
        try {
            String json = objectMapper.writeValueAsString(body);
            return baseRequest(path, requiresAuth)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
        } catch (IOException e) {
            throw new ApiException("Failed to serialize request: " + e.getMessage());
        }
    }

    private <T> T send(HttpRequest request, Class<T> responseType) throws ApiException {
        return send(request, objectMapper.getTypeFactory().constructType(responseType));
    }

    private <T> T send(HttpRequest request, com.fasterxml.jackson.databind.JavaType responseType) throws ApiException {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return objectMapper.readValue(response.body(), responseType);
            }
            throw new ApiException(extractErrorMessage(response));
        } catch (IOException e) {
            throw new ApiException("Could not reach the dental clinic server. Is it running? (" + e.getMessage() + ")");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request was interrupted");
        }
    }

    private String extractErrorMessage(HttpResponse<String> response) {
        try {
            ApiErrorDto error = objectMapper.readValue(response.body(), ApiErrorDto.class);
            return error.message() != null ? error.message() : "Request failed with status " + response.statusCode();
        } catch (IOException e) {
            return "Request failed with status " + response.statusCode();
        }
    }

    private record LoginRequestDto(String username, String password) {
    }
}
