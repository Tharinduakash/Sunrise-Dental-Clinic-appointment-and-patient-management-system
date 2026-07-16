package lk.icbt.sunrise.dental.client;

/**
 * Separate entry point so the JVM's classpath launch check doesn't see the
 * main class itself extending javafx.application.Application - that specific
 * case is blocked with a "JavaFX runtime components are missing" error even
 * when the JavaFX jars are present, unless launched via the module path.
 */
public class Launcher {
    public static void main(String[] args) {
        ClinicClientApp.main(args);
    }
}
