import java.io.Serializable;

public class ServerMessage implements Serializable {

    static final long serialVersionUID = 4;
    private String message;
    private String specialWord;

    public String getMessage() {
        return message;
    }

    public String getSpecialWord() {
        return specialWord;
    }

    public ServerMessage(String message, String specialWord){
        this.message = message;
        this.specialWord = specialWord;
    }
}
