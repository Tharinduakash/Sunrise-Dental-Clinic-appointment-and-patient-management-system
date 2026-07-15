package lk.icbt.sunrise.dental.client;

import javafx.application.Application;
import javafx.stage.Stage;
import lk.icbt.sunrise.dental.client.view.LoginView;

/**
 * Entry point for the Sunrise Dental Clinic desktop client. This is a genuinely
 * separate distributable artifact from the backend - it only ever talks to it over
 * HTTP/JSON via {@link ApiClient} - which is what makes the overall system a
 * distributed application rather than a single monolithic desktop program.
 */
public class ClinicClientApp extends Application {

    public static final String DEFAULT_BASE_URL = "http://localhost:8080";

    @Override
    public void start(Stage primaryStage) {
        String baseUrl = System.getProperty("dental.api.baseUrl", DEFAULT_BASE_URL);
        ApiClient apiClient = new ApiClient(baseUrl);
        ClientContext context = new ClientContext(primaryStage, apiClient);

        primaryStage.setResizable(true);
        LoginView.show(context);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
