package partage;

import java.io.Serializable;

public interface Task extends Serializable {
    Object execute();
    Callback getCallback();
    int getRetryCount();
    void incrementRetryCount();
}