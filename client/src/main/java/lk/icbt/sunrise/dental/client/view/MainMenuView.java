package lk.icbt.sunrise.dental.client.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lk.icbt.sunrise.dental.client.ClientContext;

import java.util.Optional;

/**
 * Menu-driven home screen (as required by the brief) that fans out to the six
 * mandatory functions plus the daily report extra feature.
 */
public final class MainMenuView {

    private MainMenuView() {
    }

    public static void show(ClientContext ctx) {
        Label title = new Label("Sunrise Dental Clinic");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));

        String fullName = ctx.currentUser() != null ? ctx.currentUser().fullName() : "Staff";
        String role = ctx.currentUser() != null ? ctx.currentUser().role() : "";
        Label welcome = new Label("Signed in as " + fullName + " (" + role + ")");
        welcome.setTextFill(Color.GRAY);

        Button registerBtn = menuButton("Register New Appointment", () -> RegisterAppointmentView.show(ctx));
        Button searchBtn = menuButton("Display Appointment Details", () -> SearchAppointmentView.show(ctx));
        Button billBtn = menuButton("Calculate and Print Bill", () -> BillView.show(ctx));
        Button reportBtn = menuButton("Daily Report (extra feature)", () -> DailyReportView.show(ctx));
        Button helpBtn = menuButton("Help", () -> HelpView.show(ctx));
        Button exitBtn = menuButton("Exit System", () -> confirmExit(ctx));

        Button logoutBtn = new Button("Log Out");
        logoutBtn.setOnAction(e -> {
            ctx.api().logout();
            LoginView.show(ctx);
        });

        VBox menu = new VBox(10, registerBtn, searchBtn, billBtn, reportBtn, helpBtn, exitBtn);
        menu.setAlignment(Pos.CENTER);
        menu.setMaxWidth(320);

        HBox topBar = new HBox(welcome);
        topBar.setAlignment(Pos.CENTER_RIGHT);

        VBox layout = new VBox(20, title, welcome, menu, logoutBtn);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(30));

        ctx.stage().setScene(new Scene(layout, 480, 480));
        ctx.stage().setTitle("Sunrise Dental Clinic - Main Menu");
    }

    private static Button menuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> action.run());
        return button;
    }

    private static void confirmExit(ClientContext ctx) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to close the application?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Exit System");
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            Platform.exit();
        }
    }
}
