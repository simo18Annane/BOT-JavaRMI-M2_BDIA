package worker;

import partage.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerManager {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i=1; i<=10; i++) {
            executorService.submit(() -> {
                try {
                    Worker worker = new WorkerImpl();
                    worker.executeTask();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdown();
            System.out.println("Fermeture des workers.");
        }));
    }
}