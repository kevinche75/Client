public class JsonException extends RuntimeException {
    protected JsonException(String message){
        super("\nПри попытке парсинга " + message+"\n===");
    }
}
