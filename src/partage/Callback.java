package partage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Callback extends Remote {
    void onResult(Object result) throws RemoteException;
}