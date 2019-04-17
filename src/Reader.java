import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Reader {



    static public CopyOnWriteArrayList<Alice> justReadFile(String path)throws FileNotFoundException{
        return readFile(path);
    }

//    static private String getFilePath(Scanner scanner) {
//        String path = System.getenv("COLLECTION_PATH");
//        if (path == null) {
//            System.out.println("===\nПуть через переменную окружения COLLECTION_PATH не указан\nНапишите адрес вручную(в консоль)");
//            path = scanner.nextLine();
//        }
//        while(true) {
//            System.out.println("===\nВведеный адрес " + path + ".\nЕсли адрес верный введите \"yes\", иначе \"no\" и повторите ввод: ");
//            switch (scanner.nextLine()){
//                case "yes":
//                    return path;
//                case "no":
//                    System.out.println("===");
//                    path = scanner.nextLine();
//                    break;
//                default:
//                    System.out.println("===\nНеизвестный ответ");
//                    break;
//            }
//        }
//    }

    static private void checkFile(File file) throws FileNotFoundException {
        if(!file.exists()) throw new FileNotFoundException("===\nФайл не существует");
        if(!file.isFile()) throw new FileNotFoundException("===\nЭто не файл");
        if(!file.canRead()) throw new SecurityException("===\nНевозможно считать файл");
    }

   static private CopyOnWriteArrayList<Alice> readFile(String path) throws FileNotFoundException{
        StringBuilder readablestring = new StringBuilder();
        int stringChar;
        File file = new File(path);
        checkFile(file);
        return jsonToLinkedList(readJsonFromFile(file));
    }

   static private String readJsonFromFile(File file) throws FileNotFoundException{
        StringBuilder line = new StringBuilder();
        int stringChar;
        try(FileReader reader = new FileReader(file)){
            while((stringChar=reader.read())!=-1){
                line.append((char) stringChar);
            }
            System.out.println("===\nФайл считан");
        } catch (IOException e){
            throw new FileNotFoundException("===\nНепредвиденная ошибка чтения файла");
        }
        return line.toString();
    }

   static private CopyOnWriteArrayList<Alice> jsonToLinkedList(String rawJson){
        UrodJsonParser gson = new UrodJsonParser();
        CopyOnWriteArrayList <Alice> linkedalices = gson.getArraysofAliceObjects(rawJson);
        linkedalices.sort(Comparator.naturalOrder());
        System.out.println("===\nЭлементов было считано: "+linkedalices.size());
        return linkedalices;
    }


}
