package serveur;

import partage.*;

import java.rmi.*;
import java.rmi.server.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;

public class BagOfTasks extends UnicastRemoteObject implements Bot {
    private static BlockingQueue<Task> taskQueue;
    private static BlockingQueue<ResultCallback> resultQueue;

    private final Map<String, Long> workersLastHeartbeat = new HashMap<>();
    private final int heartbeatTimeout = 5000;
    private final Map<String, Task> tasksInProgress = new ConcurrentHashMap<>();

    public BagOfTasks() throws RemoteException {
        super();
        this.taskQueue = new LinkedBlockingQueue<>(100); //Limite de 100 tâches
        this.resultQueue = new LinkedBlockingQueue<>(100); //Limite de 100 résultats

        //lancer le Thread pour surveiller les workers 
        new Thread(this::monitorWorkers).start();

        //Thread pour exécuter les callbacks
        new Thread(() -> {
            while (true){
                try {
                    ResultCallback resultCallback = resultQueue.take();
                    executeCallback(resultCallback);
                } catch (InterruptedException e){
                    System.err.println("Erreur lors de la surveillance de la ResultQueue: " + e.getMessage());
                }
            }
        }).start();
    }

    @Override
    public synchronized void registerHeartbeat(String workerId) throws RemoteException {
        workersLastHeartbeat.put(workerId, System.currentTimeMillis());
        System.out.println("BagOfTasks : Heartbeat reçu de " + workerId);
    }

    //surveiller les workers
    private void monitorWorkers() {
        while (true) {
            try {
                long currentTime = System.currentTimeMillis();
                Iterator<Map.Entry<String, Long>> iterator = workersLastHeartbeat.entrySet().iterator();
    
                while (iterator.hasNext()) {
                    Map.Entry<String, Long> entry = iterator.next();
                    String workerId = entry.getKey();
                    long lastHeartbeat = entry.getValue();
    
                    // Détecte l'absence de heartbeat
                    if (currentTime - lastHeartbeat > heartbeatTimeout) {
                        System.err.println("Worker " + workerId + " ne répond plus. Tentative de réassignation des tâches.");
                        iterator.remove(); 
                        reassignWorkerTasks(workerId);
                    }
                }
                Thread.sleep(heartbeatTimeout / 2);
            } catch (InterruptedException e) {
                System.err.println("Erreur lors de la surveillance des workers : " + e.getMessage());
            }
        }
    }
    
    //remettre la tache non executée dans la queue taskQueue
    private void reassignWorkerTasks(String workerId) {
        try  {
            Task failedTask = getFailedTaskForWorker(workerId);
            if (failedTask != null) {
                taskQueue.put(failedTask);
                System.out.println("tâche réassignée avec succès après détection de la panne du worker " + workerId);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la réassignation des tâches d'un worker " + workerId + " : " +e.getMessage());
        }
    }

    @Override
    public synchronized void registerTaskInProgress(String workerId, Task task) {
        tasksInProgress.put(workerId, task);
        System.out.println("BagOfTasks : Tâche enregistrée pour le worker " + workerId);
    }
    
    @Override
    public synchronized void clearTaskInProgress(String workerId) {
        tasksInProgress.remove(workerId);
        System.out.println("BagOfTasks : Tâche supprimée pour le worker " + workerId);
    }


    public Task getFailedTaskForWorker(String workerId) {
        return tasksInProgress.get(workerId);
    }
    
    
    @Override
    public void sendTask(Task t) throws RemoteException{
        //this.taskQueue.add(t);
        try {
            taskQueue.put(t);  //Bloque si la queue est pleine
            System.out.println("Tâche ajoutée à la file d'attente");
        } catch (InterruptedException e) {
            System.err.println("Erreur lors de l'ajout de la tâche : " + e.getMessage());
        }
    }

    @Override
    public Task getTask() throws RemoteException{
        //return this.taskQueue.poll(); //retourne null si la file est vide
        try {
            return taskQueue.take(); //Bloque si aucune tâche n'est disponible
        } catch (InterruptedException e) {
            System.err.println("Erreur lors de la récuperation de la tâche : " + e.getMessage());
            return null;
        }
    }

    @Override
    public void sendResult(ResultCallback result) throws RemoteException {
        //this.resultQueue.add(result);
        try {
            resultQueue.put(result); //Bloque si la queue est pleine
        } catch (InterruptedException e) {
            System.out.println("Erreur lors de l'ajout du résultat : " + e.getMessage());
        }
    }

/*
    @Override
    public ResultCallback getResult() throws RemoteException {
        return resultQueue.poll();
    }*/

    private void executeCallback(ResultCallback resCallback) {
        try {
            Callback callback = resCallback.getCallback();
            Object result = resCallback.getResult();

            callback.onResult(result);
            System.out.println("Callback exécuté avec succès.");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution du callback: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void main (String[] args){
        String name = "BOT";
        try {
            Bot bot = new BagOfTasks();
            Naming.rebind(name, bot);
            System.out.println("BagOfTasks enregistré");
        } catch (Exception e) {
            System.err.println("BagOfTasks exception : " + e.getMessage());
            e.printStackTrace();
        }
    }
}