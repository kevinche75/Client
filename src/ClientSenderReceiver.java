import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ClientSenderReceiver <T> extends Thread{

    private DatagramChannel channel;
    private  ByteArrayOutputStream sendedstreambuffer;
    private T argument;
    private String command;
    private SocketAddress address;

    public ClientSenderReceiver (DatagramChannel channel, SocketAddress adress,String command, T argument) {
        this.command = command;
        this.argument = argument;
        this.channel = channel;
        this.address = adress;
    }

    @Override
    public void run() {
            ClientMessage message = new ClientMessage(command, argument);
            sendedstreambuffer = new ByteArrayOutputStream();
            try (ObjectOutputStream sendedstream = new ObjectOutputStream(sendedstreambuffer)) {
                sendedstream.writeObject(message);
                sendedstream.flush();
                ByteBuffer buffer = ByteBuffer.wrap(sendedstreambuffer.toByteArray());
                channel.send(buffer, address);
                buffer.clear();
                System.out.println("===\nСообщение послано");
            } catch (IOException e) {
                System.out.println("===\nНепредвиденная ошибка");
            }
    }

    public void exit(){
        ClientMessage message = new ClientMessage(command, argument);
        sendedstreambuffer = new ByteArrayOutputStream();
        try (ObjectOutputStream sendedstream = new ObjectOutputStream(sendedstreambuffer)) {
            sendedstream.writeObject(message);
            sendedstream.flush();
            ByteBuffer buffer = ByteBuffer.wrap(sendedstreambuffer.toByteArray());
            channel.send(buffer, address);
            buffer.clear();
            System.out.println("===\nСообщение послано");
        } catch (IOException e) {
            System.out.println("===\nНепредвиденная ошибка");
        }
    }
}