import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Receiver extends Thread {

    private DatagramChannel channel;
    private ConsoleReader reader;

    public Receiver(ConsoleReader reader, DatagramChannel channel) {
        this.channel = channel;
        this.reader = reader;
    }

    @Override
    public void run() {
            while (!isInterrupted()) {
                try {
                    ByteBuffer buffer = ByteBuffer.wrap(new byte[64 * 1024]);
                    channel.receive(buffer);
                    try (ObjectInputStream receivedstream = new ObjectInputStream(new ByteArrayInputStream(buffer.array()))) {
                        ServerMessage message = (ServerMessage) receivedstream.readObject();
                        switch (message.getSpecialWord()) {
                            case "DISCONNECTION":
                                reader.setWorkable(false);
                                reader.setGetMessage(false);
                                reader.setTrueRegistration(false);
                                reader.setGetLogin(false);
                                reader.setLogined(false);
                                System.out.println(message.getMessage());
                                System.out.println("Введите что-нибудь для продолжения");
                                break;
                            case "MAX_NUMBER":
                                reader.setWorkable(false);
                                reader.setGetMessage(true);
                                System.out.println(message.getMessage());
                                break;
                            case "CONNECTION":
                                reader.setGetMessage(true);
                                reader.setWorkable(true);
                                System.out.println(message.getMessage());
                                break;
                            case "TRUE_LOGIN":
                                reader.setGetLogin(true);
                                reader.setLogined(true);
                                reader.setToken(Integer.parseInt(message.getMessage()));
                                break;
                            case "FALSE_LOGIN":
                                reader.setGetLogin(true);
                                reader.setLogined(false);
                                System.out.println(message.getMessage());
                                break;
                            case "FALSE_REGISTER_LOGIN":
                                reader.setGetLogin(true);
                                reader.setTrueRegistration(false);
                                System.out.println(message.getMessage());
                                break;
                            case "TRUE_REGISTER_LOGIN":
                                reader.setGetLogin(true);
                                reader.setTrueRegistration(true);
                                System.out.println(message.getMessage());
                                break;
                            default:
                                System.out.println(message.getMessage());
                        }
                    } catch (ClassNotFoundException e) {
                        System.out.println("===\nНеизвестное сообщение от сервера");
                    }
                } catch (IOException e) {
                    System.out.println("===\nНепредвиденная ошибка приёма пакетов");
                }
            }
        System.out.println("Receiver остановлен");
    }
}
