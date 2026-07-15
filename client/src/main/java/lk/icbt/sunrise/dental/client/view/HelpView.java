package lk.icbt.sunrise.dental.client.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lk.icbt.sunrise.dental.client.ClientContext;

/**
 * Function 5 from the assessment brief: Help Section.
 * Step-by-step instructions for new staff on how to use the system.
 */
public final class HelpView {

    private static final String HELP_TEXT = """
            SUNRISE DENTAL CLINIC - STAFF QUICK GUIDE

            1. LOGGING IN
               Enter your assigned username and password on the login screen and click "Log In".
               Only registered staff accounts can access the system.

            2. REGISTERING A NEW APPOINTMENT
               From the Main Menu, click "Register New Appointment".
               Fill in the patient's name, address, contact number, choose the dentist and
               treatment type, then pick a date and time. Click "Register Appointment".
               Write down the Appointment Number shown in the confirmation - the patient
               will need it for their next visit.
               The system will not allow two appointments for the same dentist at the same
               date and time, so a booking conflict will show a clear error instead of
               silently double-booking.

            3. FINDING AN EXISTING APPOINTMENT
               From the Main Menu, click "Display Appointment Details", enter the
               Appointment Number, and click "Search" to see the full patient and
               appointment record.

            4. GENERATING AND PRINTING A BILL
               From the Main Menu, click "Calculate and Print Bill", enter the Appointment
               Number, and click "Generate Bill". The consultation fee and treatment cost
               are calculated automatically based on the treatment type. Click
               "Print / Save Receipt" to print it, or save it as a text file if no printer
               is available.

            5. DAILY REPORT
               From the Main Menu, click "Daily Report (extra feature)" to see all
               appointments booked for a chosen date and the estimated revenue for that day.

            6. LOGGING OUT / EXITING
               Use "Log Out" to sign out without closing the application, or "Exit System"
               on the Main Menu to close it completely.

            If you run into an error message you don't understand, note down the exact
            text and contact the system administrator.
            """;

    private HelpView() {
    }

    public static void show(ClientContext ctx) {
        Label title = new Label("Help");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        TextArea textArea = new TextArea(HELP_TEXT);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(20);

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> MainMenuView.show(ctx));

        VBox layout = new VBox(14, title, scrollPane, backButton);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_LEFT);

        ctx.stage().setScene(new Scene(layout, 560, 520));
        ctx.stage().setTitle("Sunrise Dental Clinic - Help");
    }
}
