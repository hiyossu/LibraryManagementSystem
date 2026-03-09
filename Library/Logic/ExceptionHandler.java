package Library.Logic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ExceptionHandler — extends RuntimeException so it can be thrown,
 * caught, and logged throughout the application.
 *
 * Error code ranges:
 *   1000-1099  Input / field validation
 *   1100-1199  ISBN / DDS format
 *   1200-1299  Date validation
 *   1300-1399  ID validation
 *   2000-2099  Database / persistence
 *   9999       Generic / unknown
 */
public class ExceptionHandler extends RuntimeException {

    // ── Error code constants ───────────────────────────────────────────
    public static final int ERR_NULL_INPUT        = 1000;
    public static final int ERR_EMPTY_INPUT       = 1001;
    public static final int ERR_MAX_LENGTH        = 1002;
    public static final int ERR_REQUIRED_FIELD    = 1003;
    public static final int ERR_INVALID_ISBN      = 1100;
    public static final int ERR_INVALID_DDS       = 1101;
    public static final int ERR_INVALID_PAGES     = 1102;
    public static final int ERR_INVALID_DATE      = 1200;
    public static final int ERR_INVALID_ID        = 1300;
    public static final int ERR_DB_OPERATION      = 2000;
    public static final int ERR_BORROWER_NOT_FOUND = 2001;
    public static final int ERR_BOOK_NOT_FOUND    = 2002;
    public static final int ERR_BORROW_LIMIT      = 2003;
    public static final int ERR_BOOK_UNAVAILABLE  = 2004;
    public static final int ERR_GENERIC           = 9999;

    // ── Severity levels ────────────────────────────────────────────────
    public enum Severity { INFO, WARNING, ERROR, CRITICAL }

    // ── Fields ─────────────────────────────────────────────────────────
    private final int           errorCode;
    private final LocalDateTime timeGenerated;
    private final Severity      severity;

    // ── Constructors ───────────────────────────────────────────────────
    public ExceptionHandler(int errorCode, String errorMessage) {
        this(errorCode, errorMessage, Severity.ERROR);
    }

    public ExceptionHandler(int errorCode, String errorMessage, Severity severity) {
        super(errorMessage);
        this.errorCode     = errorCode;
        this.severity      = severity;
        this.timeGenerated = LocalDateTime.now();
    }

    // ── Static factory helpers ─────────────────────────────────────────
    public static ExceptionHandler nullInput()       { return new ExceptionHandler(ERR_NULL_INPUT,     "Input cannot be null.",                           Severity.ERROR);    }
    public static ExceptionHandler emptyInput()      { return new ExceptionHandler(ERR_EMPTY_INPUT,    "Input cannot be empty.",                          Severity.ERROR);    }
    public static ExceptionHandler maxLength()       { return new ExceptionHandler(ERR_MAX_LENGTH,     "Input exceeds maximum length of 200 characters.", Severity.WARNING);  }
    public static ExceptionHandler requiredField(String field) {
        return new ExceptionHandler(ERR_REQUIRED_FIELD, field + " is required.",                       Severity.ERROR);
    }
    public static ExceptionHandler invalidIsbn()     { return new ExceptionHandler(ERR_INVALID_ISBN,   "ISBN must be 10 or 13 digits.",                   Severity.ERROR);    }
    public static ExceptionHandler invalidDds()      { return new ExceptionHandler(ERR_INVALID_DDS,    "DDS Code must follow format: 005 or 005.13.",      Severity.ERROR);    }
    public static ExceptionHandler invalidPages()    { return new ExceptionHandler(ERR_INVALID_PAGES,  "Page count must be greater than 0.",              Severity.ERROR);    }
    public static ExceptionHandler invalidDate()     { return new ExceptionHandler(ERR_INVALID_DATE,   "Publication date cannot be in the future.",        Severity.ERROR);    }
    public static ExceptionHandler invalidId()       { return new ExceptionHandler(ERR_INVALID_ID,     "ID must be a positive number.",                   Severity.ERROR);    }
    public static ExceptionHandler dbError(String detail) {
        return new ExceptionHandler(ERR_DB_OPERATION, "Database error: " + detail,                    Severity.CRITICAL);
    }
    public static ExceptionHandler borrowerNotFound(int id) {
        return new ExceptionHandler(ERR_BORROWER_NOT_FOUND, "Borrower ID " + id + " not found.",      Severity.ERROR);
    }
    public static ExceptionHandler bookNotFound(int id) {
        return new ExceptionHandler(ERR_BOOK_NOT_FOUND,     "Book ID " + id + " not found.",          Severity.ERROR);
    }
    public static ExceptionHandler borrowLimit() {
        return new ExceptionHandler(ERR_BORROW_LIMIT,       "Borrower has reached their loan limit.", Severity.WARNING);
    }
    public static ExceptionHandler bookUnavailable() {
        return new ExceptionHandler(ERR_BOOK_UNAVAILABLE,   "This item is not currently available for checkout.", Severity.WARNING);
    }

    // ── Logging ────────────────────────────────────────────────────────
    public void logError() {
        String timestamp = timeGenerated.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String entry = String.format("[%s] [%s] ERROR %d: %s",
                timestamp, severity.name(), errorCode, getMessage());
        if (severity == Severity.CRITICAL || severity == Severity.ERROR) {
            System.err.println(entry);
        } else {
            System.out.println(entry);
        }
    }

    // ── Getters ────────────────────────────────────────────────────────
    public int           getCode()          { return errorCode;     }
    public LocalDateTime getTimeGenerated() { return timeGenerated; }
    public Severity      getSeverity()      { return severity;      }

    // ── User-friendly message (no internal codes) ──────────────────────
    public String getUserMessage() { return getMessage(); }

    @Override
    public String toString() {
        return String.format("ExceptionHandler{code=%d, severity=%s, message='%s', time=%s}",
                errorCode, severity, getMessage(), timeGenerated);
    }
}