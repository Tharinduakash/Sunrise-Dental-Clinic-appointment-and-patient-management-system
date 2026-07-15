package lk.icbt.sunrise.dental.client.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lk.icbt.sunrise.dental.client.Async;
import lk.icbt.sunrise.dental.client.ClientContext;
import lk.icbt.sunrise.dental.client.dto.AppointmentRegistrationRequestDto;
import lk.icbt.sunrise.dental.client.dto.DentistDto;
import lk.icbt.sunrise.dental.client.dto.TreatmentTypeDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Function 2 from the assessment brief: Register New Appointment.
 * Loads dentists and treatment types from the backend so the form always reflects
 * the clinic's current reference data, and validates input before it ever reaches
 * the server (the server re-validates independently - this is a usability layer,
 * not the system of record for correctness).
 */
public final class RegisterAppointmentView {

    private static final Pattern CONTACT_PATTERN = Pattern.compile("^[0-9+][0-9+\\- ]{6,19}$");

    private RegisterAppointmentView() {
    }

    public static void show(ClientContext ctx) {
        Label title = new Label("Register New Appointment");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        TextField nameField = new TextField();
        TextField addressField = new TextField();
        TextField contactField = new TextField();
        ComboBox<DentistDto> dentistCombo = new ComboBox<>();
        ComboBox<TreatmentTypeDto> treatmentCombo = new ComboBox<>();
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        ComboBox<String> timeCombo = new ComboBox<>(FXCollections.observableArrayList(buildTimeSlots()));

        dentistCombo.setMaxWidth(Double.MAX_VALUE);
        treatmentCombo.setMaxWidth(Double.MAX_VALUE);
        timeCombo.setMaxWidth(Double.MAX_VALUE);

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(360);

        loadReferenceData(ctx, dentistCombo, treatmentCombo, statusLabel);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        int row = 0;
        form.addRow(row++, new Label("Patient Name*:"), nameField);
        form.addRow(row++, new Label("Address:"), addressField);
        form.addRow(row++, new Label("Contact Number*:"), contactField);
        form.addRow(row++, new Label("Dentist*:"), dentistCombo);
        form.addRow(row++, new Label("Treatment Type*:"), treatmentCombo);
        form.addRow(row++, new Label("Appointment Date*:"), datePicker);
        form.addRow(row, new Label("Appointment Time*:"), timeCombo);
        GridPane.setHgrow(nameField, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHgrow(addressField, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHgrow(contactField, javafx.scene.layout.Priority.ALWAYS);

        Button submitButton = new Button("Register Appointment");
        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> MainMenuView.show(ctx));

        submitButton.setOnAction(e -> {
            List<String> errors = validate(nameField.getText(), contactField.getText(), dentistCombo.getValue(),
                    treatmentCombo.getValue(), datePicker.getValue(), timeCombo.getValue());
            if (!errors.isEmpty()) {
                statusLabel.setTextFill(Color.CRIMSON);
                statusLabel.setText(String.join("\n", errors));
                return;
            }

            AppointmentRegistrationRequestDto request = new AppointmentRegistrationRequestDto(
                    nameField.getText().trim(),
                    addressField.getText().trim(),
                    contactField.getText().trim(),
                    dentistCombo.getValue().id(),
                    treatmentCombo.getValue().id(),
                    datePicker.getValue(),
                    LocalTime.parse(timeCombo.getValue())
            );

            submitButton.setDisable(true);
            statusLabel.setTextFill(Color.GRAY);
            statusLabel.setText("Registering appointment...");

            Async.run(
                    () -> ctx.api().registerAppointment(request),
                    appointment -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                                "Appointment registered successfully.\nAppointment Number: "
                                        + appointment.appointmentNumber()
                                        + "\nPlease give this number to the patient - it is needed to look up "
                                        + "the appointment or generate the bill later.",
                                ButtonType.OK);
                        alert.setHeaderText("Appointment Confirmed");
                        alert.showAndWait();
                        MainMenuView.show(ctx);
                    },
                    error -> {
                        submitButton.setDisable(false);
                        statusLabel.setTextFill(Color.CRIMSON);
                        statusLabel.setText(error.getMessage());
                    }
            );
        });

        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(10, submitButton, backButton);

        VBox layout = new VBox(16, title, form, statusLabel, buttons);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);

        ctx.stage().setScene(new Scene(layout, 560, 480));
        ctx.stage().setTitle("Sunrise Dental Clinic - Register Appointment");
    }

    private static void loadReferenceData(ClientContext ctx, ComboBox<DentistDto> dentistCombo,
                                           ComboBox<TreatmentTypeDto> treatmentCombo, Label statusLabel) {
        Async.run(ctx.api()::listDentists,
                dentists -> dentistCombo.setItems(FXCollections.observableArrayList(dentists)),
                error -> {
                    statusLabel.setTextFill(Color.CRIMSON);
                    statusLabel.setText("Could not load dentists: " + error.getMessage());
                });
        Async.run(ctx.api()::listTreatmentTypes,
                treatments -> treatmentCombo.setItems(FXCollections.observableArrayList(treatments)),
                error -> {
                    statusLabel.setTextFill(Color.CRIMSON);
                    statusLabel.setText("Could not load treatment types: " + error.getMessage());
                });
    }

    private static List<String> validate(String name, String contact, DentistDto dentist,
                                          TreatmentTypeDto treatment, LocalDate date, String time) {
        List<String> errors = new ArrayList<>();
        if (name == null || name.isBlank()) {
            errors.add("Patient name is required.");
        }
        if (contact == null || !CONTACT_PATTERN.matcher(contact.trim()).matches()) {
            errors.add("Enter a valid contact number (digits, spaces, + or - only).");
        }
        if (dentist == null) {
            errors.add("Please select a dentist.");
        }
        if (treatment == null) {
            errors.add("Please select a treatment type.");
        }
        if (date == null || date.isBefore(LocalDate.now())) {
            errors.add("Appointment date must be today or later.");
        }
        if (time == null) {
            errors.add("Please select an appointment time.");
        }
        return errors;
    }

    private static List<String> buildTimeSlots() {
        List<String> slots = new ArrayList<>();
        LocalTime slot = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 0);
        while (!slot.isAfter(end)) {
            slots.add(slot.toString());
            slot = slot.plusMinutes(30);
        }
        return slots;
    }
}
