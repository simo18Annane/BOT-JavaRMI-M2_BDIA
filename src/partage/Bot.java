package partage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Bot extends Remote {
    void sendTask(Task t) throws RemoteException;
    Task getTask() throws RemoteException;
    void sendResult(ResultCallback result) throws RemoteException;
    //pour la gestion du panne
    void registerHeartbeat(String workerId) throws RemoteException;
    void registerTaskInProgress(String workerId, Task task) throws RemoteException;
    void clearTaskInProgress(String workerId) throws RemoteException;
}