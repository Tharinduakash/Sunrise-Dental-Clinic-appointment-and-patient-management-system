package lk.icbt.sunrise.dental.client;

import javafx.stage.Stage;
import lk.icbt.sunrise.dental.client.dto.LoginResponseDto;

/**
 * Shared state passed between screens: the JavaFX primary stage (so any screen can
 * swap the scene root for simple menu-driven navigation), the API client holding the
 * authenticated session, and the currently logged-in staff member.
 */
public class ClientContext {

    private final Stage primaryStage;
    private final ApiClient apiClient;
    private LoginResponseDto currentUser;

    public ClientContext(Stage primaryStage, ApiClient apiClient) {
        this.primaryStage = primaryStage;
        this.apiClient = apiClient;
    }

    public Stage stage() {
        return primaryStage;
    }

    public ApiClient api() {
        return apiClient;
    }

    public LoginResponseDto currentUser() {
        return currentUser;
    }

    public void setCurrentUser(LoginResponseDto currentUser) {
        this.currentUser = currentUser;
    }
}
