package lk.icbt.sunrise.dental.client;

import javafx.concurrent.Task;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Runs a network call off the JavaFX Application Thread so the UI never freezes while
 * waiting on the backend, then marshals the result (or failure) back onto the UI thread.
 */
public final class Async {

    private Async() {
    }

    public static <T> void run(Callable<T> backgroundWork, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return backgroundWork.call();
            }
        };
        task.setOnSucceeded(e -> onSuccess.accept(task.getValue()));
        task.setOnFailed(e -> onError.accept(task.getException()));

        Thread thread = new Thread(task, "api-call");
        thread.setDaemon(true);
        thread.start();
    }
}
