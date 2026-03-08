package Library.Logic;
import java.time.LocalDateTime;
 
public class ExceptionHandler {
    private int errorCode;
    private String errorMessage;
    private LocalDateTime timeGenerated;
 
    public ExceptionHandler(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timeGenerated = LocalDateTime.now();
    }
 
    public String getMessage(){
        return errorMessage;
    }
    public int getCode(){
        return errorCode;
    }
 
    public LocalDateTime getTimeGenerated() {
        return timeGenerated;
    }
 
    public void logError() {
        System.err.println("[" + timeGenerated + "] ERROR " + errorCode + ": " + errorMessage);
    }
}