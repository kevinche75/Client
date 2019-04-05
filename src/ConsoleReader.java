import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConsoleReader {

    private ClientSenderReceiver sender;
    private Boolean workable = false;
    private Scanner scaner;
    private DatagramChannel channel;
    private SocketAddress adress;
    private boolean work = true;
    private boolean getMessage = false;

    public ConsoleReader()  {
    scaner = new Scanner(System.in);
        int DEFAULT_PORT = 2000;
        adress = new InetSocketAddress("localhost", DEFAULT_PORT);
}

    public void setGetMessage(boolean getMessage) {
        this.getMessage = getMessage;
    }

    public void getPort(){
        System.out.println("===\nВведите порт, который вы хотите установить");
        while(true){
            String port = scaner.nextLine().trim();
            if(port.matches("[0-9]+")){
                try {
                    adress = new InetSocketAddress("localhost", Integer.parseInt(port));
                    channel = DatagramChannel.open();
                    channel.bind(adress);
                    return;
                }catch (IOException e){
                    System.out.println("===\nПорт недоступен");
                }catch (IllegalArgumentException e){
                    System.out.println("===\nПревышен максимальный порог");
                }
            } else System.out.println("===\nНеправильный формат порта");
        }
    }

    public void setWorkable(boolean workable){
    this.workable = workable;
}

    private void checkConnection() throws InterruptedException {
    System.out.println("===\nДоступные команды: " +
            "\n1. help: показать доступные комманды" +
            "\n2. connect {port}: попытка соединения с сервером" +
            "\n3. exit: выйти из приложения");
    while(true) {
        if (!workable){
            String commands[] = scaner.nextLine().trim().split(" ", 2);
            switch (commands[0].trim()) {
                case "help":
                    System.out.println("===\n1. help: показать доступные комманды" +
                            "\n2. connect: попытка соединения с сервером" +
                            "\n3. exit: выйти из приложения");
                    break;
                case "connect":
                    if(commands.length < 2 || !commands[1].trim().matches("[0-9]+")){
                        System.out.println("===\nНеправильно указан порт");
                    } else {
                        System.out.println("===\nПопытка соединения к порту " + Integer.parseInt(commands[1]));
                        getMessage = false;
                        int time = 0;
                        try {
                            adress = new InetSocketAddress("localhost", Integer.parseInt(commands[1]));
                            new ClientSenderReceiver(channel, adress, "connect", null).start();
                            while (!getMessage && time < 10000) {
                                Thread.sleep(1000);
                                System.out.println("Ждём...");
                                time += 1000;
                            }
                            if (workable) {
                                System.out.println("===\nСоединение установлено");
                                return;
                            } else {
                                if (!getMessage)
                                    System.out.println("===\nОтвет от сервера не получен. \nВозможно ответ придёт позже. \nВы можете повторить попытку соединения");
                                else System.out.println("===\nВы можете повторить попытку соединения");
                            }
                        }catch (IllegalArgumentException e) {
                            System.out.println("===\nПревышен максимальный порог");
                        }
                    }
                    break;
                case "exit":
                    work = false;
                    return;
                default:
                    System.out.println("===\nНеизвестная команда");
                    break;
            }
        } else return;
    }
}


private void shootDown(){
     Runtime.getRuntime().addShutdownHook(new Thread(()->{
         if(workable) new ClientSenderReceiver(channel,adress,"exit", null).exit();
         System.out.println("Goodbye");
     }));
}

public void work() throws InterruptedException {
    System.out.println("Hello");
    getPort();
    Receiver receiver = new Receiver(this, channel);
    receiver.setDaemon(true);
    receiver.start();
    shootDown();
    while(work){
        if(workable) {
            help();
            while (scanAndExecuteCommands()&&workable) ;
        }else{
            checkConnection();
        }
    }
}

private void load(){
    try {
        new ClientSenderReceiver<CopyOnWriteArrayList<Alice>>(channel, adress, "import", Reader.justReadFile(scaner)).start();
    } catch (FileNotFoundException e) {
        System.out.println(e.getMessage());
    }
}


    private boolean scanAndExecuteCommands() {
            String commands[] = scaner.nextLine().trim().split(" ", 2);
            switch (commands[0].trim()) {
                case "show":
                case "info":
                case "reorder":
                case "save":
                case "load":
                    if(workable) {
                        if (commands.length > 1) {
                            System.out.println("===\nДанная команда не должна содержать аргументов\n===");
                            return true;
                        }
                        new ClientSenderReceiver(channel, adress, commands[0], null).start();
                    }
                    return true;
                case "add":
                case "remove_greater":
                case "remove_all":
                case "remove":
                    if(workable) {
                        try {
                            if(commands.length==2) new ClientSenderReceiver<Alice>(channel, adress, commands[0], getElement(commands[1])).start();
                                else new ClientSenderReceiver<Alice>(channel, adress, commands[0], getElement(scaner.nextLine())).start();
                        } catch (JsonException e) {
                            System.out.println("===\nОбнаружена ошибка при парсинге элемента" + e.getMessage());
                        }
                    }
                    return true;
                case "exit":
                    if(workable) {
                        workable = false;
                        new ClientSenderReceiver(channel,adress,"exit", null).start();
                    }
                    return false;
                case "import":
                    if(workable) {
                        load();
                    }
                    return true;
                case "help":
                    if(workable) {
                        help();
                    }
                    return true;
                default:
                    if(workable) {
                        System.out.println("===\nНеизвестная команда");
                    }
                    return true;
            }
    }

    private void help(){
        System.out.println("===\nСписок доступных команд:\n" +
                "1. help: показать доступные команды\n" +
                "2. import: загрузить коллекцию на сервер\n" +
                "3. load: загрузить коллекцию из файла сервера\n" +
                "4. reorder: отсортировать коллекцию в порядке, обратном нынешнему\n" +
                "5. add {element}: добавить новый элемент в коллекцию, элемент должен быть введён в формате json\n" +
                "6. remove_greater {element}: удалить из коллекции все элементы, превышающие заданный, элемент должен быть введён в формате json\n" +
                "7. show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "8. info: вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д., элемент должен быть введён в формате json)\n" +
                "9. remove_all {element}: удалить из коллекции все элементы, эквивалентные заданному, элемент должен быть введён в формате json\n" +
                "10. remove {element}: удалить элемент из коллекции по его значению, элемент должен быть введён в формате json\n" +
                "11. exit: отключение от сервера\n" +
                "Пример элемента: {\n" +
                "  \"politeness\": \"RUDE\",\n" +
                "  \"size\": 1245,\n" +
                "  \"condition\": \"NORMAL\",\n" +
                "  \"date\": \"12.04.2124\",\n" +
                "  \"cap\": {\n" +
                "  \t\"nameOfUser\": \"Алисt\"\n" +
                "\t\"fullness\": 122\n" +
                "  },\n" +
                "  \"name\": \"Алисt\",\n" +
                "  \"x\": 10\n" +
                "  }");
    }

    private Alice getElement(String rawjson){
        int counterleft = getScobochki1(rawjson,'{');
        int counterright = getScobochki1(rawjson,'}');
        StringBuilder rawjsonBuilder = new StringBuilder(rawjson);
        while(!(counterleft==counterright)){
            String s = scaner.nextLine();
            counterleft += getScobochki1(s,'{');
            counterright += getScobochki1(s,'}');
            rawjsonBuilder.append(s);
        }
        rawjson = rawjsonBuilder.toString();
        rawjson = rawjson.trim();
        UrodJsonParser simpleJsonParser = new UrodJsonParser();
        return simpleJsonParser.simpleParseAliceObjects(rawjson);
    }

    private int getScobochki1(String string,char scobochka){
        int counter = 0;
        for(char c : string.toCharArray()){
            if(c==scobochka) counter++;
        }
        return counter;
    }
}
