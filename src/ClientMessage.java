import java.io.Serializable;

public class ClientMessage <T> implements Serializable {
    private String message;
    private int token;
    private T argument;
    static final long serialVersionUID = 1;

    public ClientMessage(String message, T argument, int token) {
        this.message = message;
        this.token = token;
        this.argument = argument;
    }

    public int getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public T getArgument() {
        return argument;
    }
}
