package Library.Logic;

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

    public void logError() {
        System.err.println("[" + timeGenerated + "] ERROR " + errorCode + ": " + errorMessage);
    }
}