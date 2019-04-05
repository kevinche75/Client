import java.io.Serializable;

public class ServerMessage implements Serializable {

    static final long serialVersionUID = 4;
    private String message;

    public String getMessage() {
        return message;
    }

    public ServerMessage(String message){
        this.message = message;
    }
}
