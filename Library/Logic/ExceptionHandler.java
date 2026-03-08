package Library.Logic;
<<<<<<< HEAD
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
=======

import java.time.LocalDate;

public class ExceptionHandler {

    private final int       errorCode;
    private final String    errorMessage;
    private final LocalDate timeGenerated;

    public ExceptionHandler(int errorCode, String errorMessage) {
        this.errorCode     = errorCode;
        this.errorMessage  = errorMessage;
        this.timeGenerated = LocalDate.now();
    }

    public int       getCode()          { return errorCode;     }
    public String    getMessage()       { return errorMessage;  }
    public LocalDate getTimeGenerated() { return timeGenerated; }
>>>>>>> f264304e8dc198434381c9ba3b00fb3ebce89920

    public void logError() {
        System.err.println("[" + timeGenerated + "] ERROR " + errorCode + ": " + errorMessage);
    }
}