package lk.icbt.sunrise.dental.client.view;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lk.icbt.sunrise.dental.client.Async;
import lk.icbt.sunrise.dental.client.ClientContext;
import lk.icbt.sunrise.dental.client.dto.AppointmentDto;

import java.time.LocalDate;
import java.util.function.Function;

/**
 * Extra value-add feature (beyond the six mandatory functions): a daily schedule
 * and revenue report so clinic management can see workload and income at a glance.
 */
public final class DailyReportView {

    private DailyReportView() {
    }

    public static void show(ClientContext ctx) {
        Label title = new Label("Daily Report");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        DatePicker datePicker = new DatePicker(LocalDate.now());
        Button fetchButton = new Button("Load Report");

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);

        Label summaryLabel = new Label();
        summaryLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        TableView<AppointmentDto> table = new TableView<>();
        table.getColumns().addAll(
                column("Appt No", AppointmentDto::appointmentNumber, 100),
                column("Patient", AppointmentDto::patientName, 130),
                column("Dentist", AppointmentDto::dentistName, 140),
                column("Treatment", AppointmentDto::treatmentTypeName, 150),
                column("Time", a -> a.appointmentTime().toString(), 80),
                column("Status", AppointmentDto::status, 90)
        );
        table.setPrefHeight(260);

        Runnable fetch = () -> {
            LocalDate date = datePicker.getValue();
            if (date == null) {
                statusLabel.setTextFill(Color.CRIMSON);
                statusLabel.setText("Select a date.");
                return;
            }
            statusLabel.setTextFill(Color.GRAY);
            statusLabel.setText("Loading...");

            Async.run(
                    () -> ctx.api().dailyReport(date),
                    report -> {
                        statusLabel.setText("");
                        table.setItems(FXCollections.observableArrayList(report.appointments()));
                        summaryLabel.setText(report.totalAppointments() + " appointment(s) - Estimated revenue: Rs. " + report.totalRevenue());
                    },
                    error -> {
                        statusLabel.setTextFill(Color.CRIMSON);
                        statusLabel.setText(error.getMessage());
                    }
            );
        };

        fetchButton.setOnAction(e -> fetch.run());

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> MainMenuView.show(ctx));

        HBox controls = new HBox(10, datePicker, fetchButton);

        VBox layout = new VBox(14, title, controls, statusLabel, table, summaryLabel, backButton);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);

        ctx.stage().setScene(new Scene(layout, 640, 520));
        ctx.stage().setTitle("Sunrise Dental Clinic - Daily Report");

        fetch.run();
    }

    private static TableColumn<AppointmentDto, String> column(String header, Function<AppointmentDto, String> extractor, double width) {
        TableColumn<AppointmentDto, String> column = new TableColumn<>(header);
        column.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(extractor.apply(cellData.getValue())));
        column.setPrefWidth(width);
        return column;
    }
}
