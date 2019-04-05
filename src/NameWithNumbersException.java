import java.io.Serializable;

public class NameWithNumbersException extends RuntimeException implements Serializable {
    static final long serialVersionUID = 3;
    NameWithNumbersException(){
        System.out.println("NameWithNumbersException: Имя не должно содержать цифр");
        System.exit(0);
    }
}
