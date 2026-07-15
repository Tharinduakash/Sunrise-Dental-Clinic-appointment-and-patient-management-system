package lk.icbt.sunrise.dental.client.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lk.icbt.sunrise.dental.client.Async;
import lk.icbt.sunrise.dental.client.ClientContext;
import lk.icbt.sunrise.dental.client.dto.AppointmentDto;

/**
 * Function 3 from the assessment brief: Display Appointment Details.
 * Search using the appointment number and show the complete patient and
 * appointment information.
 */
public final class SearchAppointmentView {

    private SearchAppointmentView() {
    }

    public static void show(ClientContext ctx) {
        Label title = new Label("Display Appointment Details");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        TextField numberField = new TextField();
        numberField.setPromptText("e.g. APT-000001");
        Button searchButton = new Button("Search");

        GridPane details = new GridPane();
        details.setHgap(10);
        details.setVgap(8);
        details.setPadding(new Insets(10, 0, 0, 0));

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(400);

        Runnable search = () -> {
            String number = numberField.getText().trim();
            if (number.isEmpty()) {
                statusLabel.setTextFill(Color.CRIMSON);
                statusLabel.setText("Enter an appointment number to search.");
                return;
            }
            details.getChildren().clear();
            statusLabel.setTextFill(Color.GRAY);
            statusLabel.setText("Searching...");

            Async.run(
                    () -> ctx.api().getAppointment(number),
                    appointment -> {
                        statusLabel.setText("");
                        populateDetails(details, appointment);
                    },
                    error -> {
                        statusLabel.setTextFill(Color.CRIMSON);
                        statusLabel.setText(error.getMessage());
                    }
            );
        };

        searchButton.setOnAction(e -> search.run());
        numberField.setOnAction(e -> search.run());

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> MainMenuView.show(ctx));

        HBox searchBar = new HBox(10, numberField, searchButton);

        VBox layout = new VBox(16, title, searchBar, statusLabel, details, backButton);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);

        ctx.stage().setScene(new Scene(layout, 520, 440));
        ctx.stage().setTitle("Sunrise Dental Clinic - Search Appointment");
    }

    private static void populateDetails(GridPane grid, AppointmentDto a) {
        int row = 0;
        grid.addRow(row++, boldLabel("Appointment Number:"), new Label(a.appointmentNumber()));
        grid.addRow(row++, boldLabel("Patient Name:"), new Label(a.patientName()));
        grid.addRow(row++, boldLabel("Address:"), new Label(a.address() != null ? a.address() : "-"));
        grid.addRow(row++, boldLabel("Contact Number:"), new Label(a.contactNumber()));
        grid.addRow(row++, boldLabel("Dentist:"), new Label(a.dentistName()));
        grid.addRow(row++, boldLabel("Treatment Type:"), new Label(a.treatmentTypeName()));
        grid.addRow(row++, boldLabel("Appointment Date:"), new Label(a.appointmentDate().toString()));
        grid.addRow(row++, boldLabel("Appointment Time:"), new Label(a.appointmentTime().toString()));
        grid.addRow(row, boldLabel("Status:"), new Label(a.status()));
    }

    private static Label boldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 12));
        return label;
    }
}
