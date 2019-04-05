import java.util.NoSuchElementException;

public class Client {

    public static void main(String[] args) {
        try {
            ConsoleReader consoleReader = new ConsoleReader();
            consoleReader.work();
        } catch (InterruptedException | NoSuchElementException e) {
        }
    }
}
