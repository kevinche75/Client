import com.lambdaworks.codec.Base64;
import com.lambdaworks.crypto.SCrypt;
import com.lambdaworks.crypto.SCryptUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConsoleReader {

    private Boolean workable = false;
    private Scanner scaner;
    private DatagramChannel channel;
    private SocketAddress adress;
    private boolean work = true;
    private boolean getMessage = false;
    private boolean logined = false;
    private boolean getLogin = false;
    private int token;
    private boolean trueRegistration = false;

    public ConsoleReader() {
        scaner = new Scanner(System.in);
        int DEFAULT_PORT = 2000;
        adress = new InetSocketAddress("localhost", DEFAULT_PORT);
    }

    public void setLogined(boolean logined) {
        this.logined = logined;
    }

    public void setGetLogin(boolean getLogin) {
        this.getLogin = getLogin;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public void setTrueRegistration(boolean trueRegistration) {
        this.trueRegistration = trueRegistration;
    }

    public void setGetMessage(boolean getMessage) {
        this.getMessage = getMessage;
    }

    public void getPort() {
        System.out.println("===\nВведите порт, который вы хотите установить");
        while (true) {
            String port = scaner.nextLine().trim();
            if (port.matches("[0-9]+")) {
                try {
                    adress = new InetSocketAddress("localhost", Integer.parseInt(port));
                    channel = DatagramChannel.open();
                    channel.bind(adress);
                    return;
                } catch (IOException e) {
                    System.out.println("===\nПорт недоступен");
                } catch (IllegalArgumentException e) {
                    System.out.println("===\nПревышен максимальный порог");
                }
            } else System.out.println("===\nНеправильный формат порта");
        }
    }

    public void setWorkable(boolean workable) {
        this.workable = workable;
    }

    private void checkConnection() throws InterruptedException {
        System.out.println("===\nДоступные команды: " +
                "\n1. help: показать доступные комманды" +
                "\n2. connect {port}: попытка соединения с сервером" +
                "\n3. exit: выйти из приложения");
        while (true) {
            if (!workable) {
                String commands[] = scaner.nextLine().trim().split(" ", 2);
                switch (commands[0].trim()) {
                    case "help":
                        System.out.println("===\n1. help: показать доступные комманды" +
                                "\n2. connect {port}: попытка соединения с сервером" +
                                "\n3. exit: выйти из приложения");
                        break;
                    case "connect":
                        if (commands.length < 2 || !commands[1].trim().matches("[0-9]+")) {
                            System.out.println("===\nНеправильно указан порт");
                        } else {
                            System.out.println("===\nПопытка соединения к порту " + Integer.parseInt(commands[1]));
                            getMessage = false;
                            int time = 0;
                            try {
                                adress = new InetSocketAddress("localhost", Integer.parseInt(commands[1]));
                                new ClientSenderReceiver(channel, adress, "connect", null).send();
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
                            } catch (IllegalArgumentException e) {
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


    private void shootDown() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (logined) new ClientSenderReceiver(channel, adress, "exit", null, token).send();
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
        while (work) {
            if (workable && logined) {
                help();
                while (scanAndExecuteCommands() && workable) ;
            } else {
                if (workable) loginOrRegister();
                else checkConnection();
            }
        }
    }

    private String getFilePath() {
        String path = System.getenv("COLLECTION_PATH");
        if (path == null) {
            System.out.println("===\nПуть через переменную окружения COLLECTION_PATH не указан\nНапишите адрес вручную(в консоль)");
            path = scaner.nextLine();
        }
        while(workable) {
            System.out.println("===\nВведеный адрес " + path + ".\nЕсли адрес верный введите \"yes\", иначе \"no\" и повторите ввод: ");
            switch (scaner.nextLine()){
                case "yes":
                    return path;
                case "no":
                    System.out.println("===");
                    path = scaner.nextLine();
                    break;
                default:
                    System.out.println("===\nНеизвестный ответ");
                    break;
            }
        }
        return "";
    }

    private void load() {
        try {
            String path = getFilePath();
            if(workable) {
                ClientSenderReceiver senderReceiver = new ClientSenderReceiver<CopyOnWriteArrayList<Alice>>(channel, adress, "import", Reader.justReadFile(path), token);
                if (workable) senderReceiver.send();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loginCommands() {
        System.out.println("===" +
                "\n1. help: показать доступные комманды" +
                "\n2. login: ввойти уже в существующий аккаунт" +
                "\n3. register: создать новый аккаунт" +
                "\n4. exit: выйти из окна авторизации");
    }

    private void loginOrRegister() throws InterruptedException {
        loginCommands();
        while (true) {
            logined = false;
            switch (scaner.nextLine().trim()) {
                case "help":
                    if (workable && !logined) {
                        loginCommands();
                    }
                    break;
                case "login":
                    if (workable && !logined) {
                        System.out.println("===\nВведите логин");
                        String login = scaner.nextLine().trim();
                        System.out.println("===\nВведите пароль");
                        String password = getHash(scaner.nextLine().trim());
                        Pair<String, String> loginPassword = new Pair<>(login, password);
                        new ClientSenderReceiver<>(channel, adress, "login", loginPassword).send();
                        int time = 0;
                        getLogin = false;
                        while (!getLogin && time < 100000) {
                            Thread.sleep(1000);
                            time += 1000;
                        }
                        if (!getLogin) {
                            System.out.println("===\nАвторизация не удалась, сообщение от севрера не получено, попытайтесь позже");
                        } else {
                            if (logined) {
                                System.out.println("===\nПользователь зарегистрирован");
                                return;
                            }
                        }
                    }
                    break;
                case "register":
                    if (workable && !logined) {
                        System.out.println("===\nВведите логин, который хотели бы использовать");
                        String login = scaner.nextLine().trim();
                        System.out.println("===\nВведите пароль, который хотели бы использовать");
                        String password = getHash(scaner.nextLine().trim());
                        Pair<String, String> registerPassword = new Pair<>(login, password);
                        new ClientSenderReceiver<>(channel, adress, "register", registerPassword).send();
                        int time = 0;
                        getLogin = false;
                        trueRegistration = false;
                        while (!getLogin && time < 100000) {
                            System.out.println("Ждём...");
                            Thread.sleep(1000);
                            time += 1000;
                        }
                        if (trueRegistration) {
                            System.out.println("===\nВведите код, который был отправлен на почту");
                            while(true) {
                                try {
                                    int token = Integer.parseInt(scaner.nextLine());
                                    new ClientSenderReceiver<String>(channel, adress, "token", login, token).send();
                                    break;
                                } catch (NumberFormatException e) {
                                    System.out.println("===\nНеверный формат кодового числа");
                                }
                            }
                        }
                    }
                    break;
                case "exit":
                    if (workable && !logined) {
                        workable = false;
                        return;
                    }
                    break;
                default:
                    System.out.println("===\nНеизвестная команда");
                    break;
            }
        }
    }

    private boolean scanAndExecuteCommands() {
        String commands[] = scaner.nextLine().trim().split(" ", 2);
        switch (commands[0].trim()) {
            case "show":
            case "info":
            case "reorder":
            case "show_yours":
                if (workable) {
                    if (commands.length > 1) {
                        System.out.println("===\nДанная команда не должна содержать аргументов\n===");
                        return true;
                    }
                    new ClientSenderReceiver(channel, adress, commands[0], null, token).send();
                }
                return true;
            case "add":
            case "remove_greater":
            case "remove_all":
            case "remove":
                if (workable) {
                    try {
                        if (commands.length == 2)
                            new ClientSenderReceiver<Alice>(channel, adress, commands[0], getElement(commands[1]), token).send();
                        else
                            new ClientSenderReceiver<Alice>(channel, adress, commands[0], getElement(scaner.nextLine()), token).send();
                    } catch (JsonException e) {
                        System.out.println("===\nОбнаружена ошибка при парсинге элемента" + e.getMessage());
                    }
                }
                return true;
            case "exit":
                if (workable) {
                    logined = false;
                    new ClientSenderReceiver(channel, adress, "exit", null, token).send();
                }
                return false;
            case "import":
                if (workable) {
                    load();
                }
                return true;
            case "help":
                if (workable) {
                    help();
                }
                return true;
            default:
                if (workable) {
                    System.out.println("===\nНеизвестная команда");
                }
                return true;
        }
    }

    private void help() {
        System.out.println("===\nСписок доступных команд:\n" +
                "1. help: показать доступные команды\n" +
                "2. import: загрузить коллекцию на сервер\n" +
                "3. show_yours: показать элементы, принадлежащие вам\n" +
                "4. add {element}: добавить новый элемент в коллекцию, элемент должен быть введён в формате json\n" +
                "5. remove_greater {element}: удалить из коллекции все элементы, превышающие заданный, элемент должен быть введён в формате json\n" +
                "6. show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "7. info: вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д., элемент должен быть введён в формате json)\n" +
                "8. remove_all {element}: удалить из коллекции все элементы, эквивалентные заданному, элемент должен быть введён в формате json\n" +
                "9. remove {element}: удалить элемент из коллекции по его значению, элемент должен быть введён в формате json\n" +
                "10. exit: отключение от сервера\n" +
                "Пример элемента: {\n" +
                "  \"politeness\": \"RUDE\",\n" +
                "  \"size\": 1245,\n" +
                "  \"condition\": \"NORMAL\",\n" +
                "  \"date\": \"2016-10-02T20:15:30+01:00\",\n" +
                "  \"cap\": {\n" +
                "  \t\"nameOfUser\": \"alice\"\n" +
                "\t\"fullness\": 122\n" +
                "  },\n" +
                "  \"name\": \"alice\",\n" +
                "  \"x\": 10\n" +
                "  }");
    }

    private Alice getElement(String rawjson) {
        int counterleft = getScobochki1(rawjson, '{');
        int counterright = getScobochki1(rawjson, '}');
        StringBuilder rawjsonBuilder = new StringBuilder(rawjson);
        while (!(counterleft == counterright)) {
            String s = scaner.nextLine();
            counterleft += getScobochki1(s, '{');
            counterright += getScobochki1(s, '}');
            rawjsonBuilder.append(s);
        }
        rawjson = rawjsonBuilder.toString();
        rawjson = rawjson.trim();
        UrodJsonParser simpleJsonParser = new UrodJsonParser();
        return simpleJsonParser.simpleParseAliceObjects(rawjson);
    }

    private int getScobochki1(String string, char scobochka) {
        int counter = 0;
        for (char c : string.toCharArray()) {
            if (c == scobochka) counter++;
        }
        return counter;
    }

    public static String getHash(String pass) {
        try {
            byte[] derived = SCrypt.scrypt(pass.getBytes(), "salt".getBytes(), 16, 16, 16, 32);
            return new String(Base64.encode(derived));
        } catch (Exception e) {
            return null;
        }
    }
}
