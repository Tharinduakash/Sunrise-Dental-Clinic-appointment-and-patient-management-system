package lk.icbt.sunrise.dental.client.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import lk.icbt.sunrise.dental.client.Async;
import lk.icbt.sunrise.dental.client.ClientContext;
import lk.icbt.sunrise.dental.client.dto.BillDto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;

/**
 * Function 4 from the assessment brief: Calculate and Print Bill.
 * Fetches (or triggers server-side generation of) the bill for an appointment number,
 * then offers a real print via {@link PrinterJob}, falling back to saving the receipt
 * as a text file when no printer is configured on the machine.
 */
public final class BillView {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private BillView() {
    }

    public static void show(ClientContext ctx) {
        Label title = new Label("Calculate and Print Bill");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        TextField numberField = new TextField();
        numberField.setPromptText("e.g. APT-000001");
        Button generateButton = new Button("Generate Bill");

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(420);

        VBox receiptBox = new VBox(4);
        receiptBox.setPadding(new Insets(12));
        receiptBox.setStyle("-fx-border-color: gray; -fx-border-width: 1;");
        receiptBox.setVisible(false);
        receiptBox.setManaged(false);

        Button printButton = new Button("Print / Save Receipt");
        printButton.setDisable(true);

        final BillDto[] currentBill = new BillDto[1];

        Runnable generate = () -> {
            String number = numberField.getText().trim();
            if (number.isEmpty()) {
                statusLabel.setTextFill(Color.CRIMSON);
                statusLabel.setText("Enter an appointment number.");
                return;
            }
            statusLabel.setTextFill(Color.GRAY);
            statusLabel.setText("Calculating bill...");
            generateButton.setDisable(true);
            printButton.setDisable(true);
            receiptBox.setVisible(false);
            receiptBox.setManaged(false);

            Async.run(
                    () -> ctx.api().generateBill(number),
                    bill -> {
                        generateButton.setDisable(false);
                        statusLabel.setText("");
                        currentBill[0] = bill;
                        renderReceipt(receiptBox, bill);
                        receiptBox.setVisible(true);
                        receiptBox.setManaged(true);
                        printButton.setDisable(false);
                    },
                    error -> {
                        generateButton.setDisable(false);
                        statusLabel.setTextFill(Color.CRIMSON);
                        statusLabel.setText(error.getMessage());
                    }
            );
        };

        generateButton.setOnAction(e -> generate.run());
        numberField.setOnAction(e -> generate.run());

        printButton.setOnAction(e -> printOrSaveReceipt(ctx, currentBill[0]));

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> MainMenuView.show(ctx));

        HBox searchBar = new HBox(10, numberField, generateButton);
        HBox actions = new HBox(10, printButton, backButton);

        VBox layout = new VBox(16, title, searchBar, statusLabel, receiptBox, actions);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);

        ctx.stage().setScene(new Scene(layout, 520, 480));
        ctx.stage().setTitle("Sunrise Dental Clinic - Bill / Receipt");
    }

    private static void renderReceipt(VBox box, BillDto bill) {
        box.getChildren().clear();
        Label header = new Label("SUNRISE DENTAL CLINIC - RECEIPT");
        header.setFont(Font.font("System", FontWeight.BOLD, 14));
        box.getChildren().addAll(
                header,
                new Label("Appointment No: " + bill.appointmentNumber()),
                new Label("Patient: " + bill.patientName()),
                new Label("Treatment: " + bill.treatmentTypeName()),
                new Label("Consultation Fee: Rs. " + bill.consultationFee()),
                new Label("Treatment Cost: Rs. " + bill.treatmentCost()),
                new Label("TOTAL: Rs. " + bill.totalAmount()),
                new Label("Generated: " + bill.generatedAt().format(TIMESTAMP_FORMAT))
        );
    }

    private static void printOrSaveReceipt(ClientContext ctx, BillDto bill) {
        if (bill == null) {
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(ctx.stage())) {
            VBox printNode = new VBox(4);
            renderReceipt(printNode, bill);
            boolean success = job.printPage(printNode);
            if (success) {
                job.endJob();
                new Alert(Alert.AlertType.INFORMATION, "Receipt sent to printer.").showAndWait();
                return;
            }
        }

        // No printer configured, or the user cancelled the print dialog: offer to save
        // the receipt as a text file instead so the bill is never simply lost.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(bill.appointmentNumber() + "-receipt.txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text file", "*.txt"));
        var file = fileChooser.showSaveDialog(ctx.stage());
        if (file != null) {
            try {
                Files.writeString(file.toPath(), receiptText(bill), StandardCharsets.UTF_8);
                new Alert(Alert.AlertType.INFORMATION, "Receipt saved to " + file.getName()).showAndWait();
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, "Could not save receipt: " + ex.getMessage()).showAndWait();
            }
        }
    }

    private static String receiptText(BillDto bill) {
        return """
                SUNRISE DENTAL CLINIC - RECEIPT
                Appointment No: %s
                Patient: %s
                Treatment: %s
                Consultation Fee: Rs. %s
                Treatment Cost: Rs. %s
                TOTAL: Rs. %s
                Generated: %s
                """.formatted(bill.appointmentNumber(), bill.patientName(), bill.treatmentTypeName(),
                bill.consultationFee(), bill.treatmentCost(), bill.totalAmount(),
                bill.generatedAt().format(TIMESTAMP_FORMAT));
    }
}
