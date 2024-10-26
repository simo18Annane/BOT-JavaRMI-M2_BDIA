package worker;

import partage.*;
import serveur.*;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.concurrent.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class WorkerImpl implements Worker {
    private Bot bagOT;
    private final String workerId;
    private final int timeout = 5;
    private final int maxRetries = 3;
    private Task currenTask;
    private Timer timer;

    public WorkerImpl(){
        this.workerId = UUID.randomUUID().toString();
        try {
            String name = "BOT";
            this.bagOT = (Bot) Naming.lookup(name);
            System.out.println("Worker connecté à BagOfTasks : " + name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startHeartbeat();
    }

    private void startHeartbeat() {
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try{
                    bagOT.registerHeartbeat(workerId);
                    System.out.println("Worker " + workerId + " : Heartbeat envoyé");
                } catch (RemoteException e) {
                    System.err.println("Erreur lors de l'envoi du heartbeat : " + e.getMessage());
                    timer.cancel();
                }
                
            }
        }, 0, 3000); //Battement de coeur toutes les 3 secondes
    }

    @Override
    public void executeTask() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            while (true) {
                //Récuperation de la tâche de manière bloquante
                Task task = bagOT.getTask();
                if(task != null) {
                    currenTask = task;
                    bagOT.registerTaskInProgress(workerId, task);
                    System.out.println("Worker" + workerId + " : Tâche récupérée : " + task);

                    //simuler une panne aléatoire
                    if (Math.random() < 0.3) {
                        System.err.println("Worker " + workerId + " tombe en panne!");
                        timer.cancel();
                        throw new RuntimeException("Simulation d'une panne");
                    }

                    Future<Object> futureTask = executor.submit(() -> task.execute());
                    try {
                        Object result = futureTask.get(timeout, TimeUnit.SECONDS);
                        //Envoie le résultat et le callback
                        ResultCallback resultCallback = new ResultCallback(result, task.getCallback());
                        bagOT.sendResult(resultCallback);
                        System.out.println("Résultat envoyé par le worker " + workerId);
                    } catch (TimeoutException e) {
                        System.err.println("La tâche a dépassé le délai imparti !");
                        FailedTask(task);
                    } catch (ExecutionException e) {
                        System.err.println("Erreur lors de l'exécution de la tâche !");
                        FailedTask(task);
                    } finally {
                        bagOT.clearTaskInProgress(workerId);
                        currenTask = null;
                    }
                    
                } 
                /*else {
                    System.out.println("Aucun tâche disponible, le worker attend.");
                    Thread.sleep(2000);
                }*/
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        
    }

    //Gestion de l'echec de la tache
    private void FailedTask(Task task) throws RemoteException {
        task.incrementRetryCount();
        if (task.getRetryCount() <= maxRetries) {
            System.out.println("Réassignation de la tâche. Tentative n" + task.getRetryCount());
            bagOT.sendTask(task);
        } else {
            System.err.println("La tâche a échoué apres " + maxRetries + " tantatves.");
            task.getCallback().onResult("Echec de la tâche apres plusieurs tentatives.");
        }
    }
}