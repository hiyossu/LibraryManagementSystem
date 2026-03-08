package Library.Logic;
import java.time.LocalDate;

public class ExceptionHandler {
    private int errorCode;
    private String errorMessage;
    private LocalDate timeGenerated;

    public ExceptionHandler(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timeGenerated = LocalDate.now();
    }

    public String getMessage(){
        return errorMessage;
    }
    public int getCode(){
        return errorCode;
    }

    public LocalDate getTimeGenerated() {
        return timeGenerated;
    }

    public void logError() {
        System.err.println("[" + timeGenerated + "] ERROR " + errorCode + ": " + errorMessage);
    }
}