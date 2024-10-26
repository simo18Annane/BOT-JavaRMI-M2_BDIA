package partage;

import java.io.Serializable;

public class ResultCallback implements Serializable {
    private Object result;
    private Callback callback;

    public ResultCallback(Object result, Callback callback) {
        this.result = result;
        this.callback = callback;
    }

    public Object getResult() {
        return result;
    }

    public Callback getCallback() {
        return callback;
    }
}