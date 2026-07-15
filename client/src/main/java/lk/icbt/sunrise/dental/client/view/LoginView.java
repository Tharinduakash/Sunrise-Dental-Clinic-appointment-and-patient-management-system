package lk.icbt.sunrise.dental.client.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lk.icbt.sunrise.dental.client.Async;
import lk.icbt.sunrise.dental.client.ClientContext;

/**
 * Function 1 from the assessment brief: User Authentication (Login).
 * Only staff who authenticate successfully against the backend can reach the rest
 * of the system.
 */
public final class LoginView {

    private LoginView() {
    }

    public static void show(ClientContext ctx) {
        Label title = new Label("Sunrise Dental Clinic");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));

        Label subtitle = new Label("Staff Login");
        subtitle.setFont(Font.font("System", 14));
        subtitle.setTextFill(Color.GRAY);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(260);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(260);

        Label statusLabel = new Label();
        statusLabel.setTextFill(Color.CRIMSON);
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(280);

        Button loginButton = new Button("Log In");
        loginButton.setDefaultButton(true);
        loginButton.setMaxWidth(260);

        Runnable attemptLogin = () -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter both username and password.");
                return;
            }

            loginButton.setDisable(true);
            statusLabel.setTextFill(Color.GRAY);
            statusLabel.setText("Signing in...");

            Async.run(
                    () -> ctx.api().login(username, password),
                    response -> {
                        ctx.setCurrentUser(response);
                        MainMenuView.show(ctx);
                    },
                    error -> {
                        loginButton.setDisable(false);
                        statusLabel.setTextFill(Color.CRIMSON);
                        statusLabel.setText(error.getMessage());
                    }
            );
        };

        loginButton.setOnAction(e -> attemptLogin.run());

        Label hint = new Label("Default accounts: admin / Admin@123, reception / Reception@123");
        hint.setFont(Font.font("System", 10));
        hint.setTextFill(Color.SILVER);

        VBox layout = new VBox(12, title, subtitle, usernameField, passwordField, loginButton, statusLabel, hint);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        ctx.stage().setScene(new Scene(layout, 420, 420));
        ctx.stage().setTitle("Sunrise Dental Clinic - Login");
    }
}
