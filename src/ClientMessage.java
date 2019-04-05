import java.io.Serializable;

public class ClientMessage <T> implements Serializable {
    private String message;
    private T argument;
    static final long serialVersionUID = 1;

    public ClientMessage (String message, T argument) {
        this.message = message;
        this.argument = argument;
    }

    public String getMessage() {
        return message;
    }


    public T getArgument() {
        return argument;
    }

}
