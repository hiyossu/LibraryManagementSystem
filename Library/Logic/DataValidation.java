package Library.Logic;
 
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DataValidation — collects all validation errors and bridges them to
 * ExceptionHandler so callers can log, display, or throw them uniformly.
 *
 * Typical usage:
 *   DataValidation v = new DataValidation();
 *   if (!v.checkConstraints(...)) {
 *       v.logAllErrors();
 *       String msg = v.getFirstUserMessage();   // show in status bar
 *       // or: throw v.toException();           // throw the first error
 *   }
 */
public class DataValidation {

    // ── Internal state ─────────────────────────────────────────────────
    private final List<ExceptionHandler> errors = new ArrayList<>();

    // ── Stored-field workflow ──────────────────────────────────────────
    private String    currentTitle;
    private String    currentIsbn;
    private String    currentAuthor;
    private String    currentDeweyDecimal;
    private int       currentPages;
    private String    currentType;
    private String    currentGenre;
    private LocalDate currentYearPublished;

    // ══════════════════════════════════════════════════════════════════
    //  PUBLIC VALIDATION METHODS
    // ══════════════════════════════════════════════════════════════════

    /**
     * Validate a single free-text input against null / empty / length rules.
     */
    public boolean validateInput(String data) {
        clearErrors();
        if (data == null)                { addError(ExceptionHandler.nullInput());  return false; }
        if (data.trim().isEmpty())       { addError(ExceptionHandler.emptyInput()); return false; }
        if (data.trim().length() > 200)  { addError(ExceptionHandler.maxLength());  return false; }
        return true;
    }

    /**
     * Full constraint check for a book / media item.
     * Collects ALL errors before returning so the caller sees everything wrong at once.
     */
    public boolean checkConstraints(String isbn, String title, String author,
                                    String ddsCode, int pages, LocalDate yearPublished,
                                    String type, String genre) {
        clearErrors();

        // ISBN — required for Book / ReferenceBook; skip for media types that pass null
        if (isbn != null && !isbn.replaceAll("-", "").trim().matches("\\d{10}|\\d{13}")) {
            addError(ExceptionHandler.invalidIsbn());
        }

        if (isNullOrBlank(title))  addError(ExceptionHandler.requiredField("Title"));
        if (isNullOrBlank(author)) addError(ExceptionHandler.requiredField("Author"));

        if (ddsCode == null || !ddsCode.trim().matches("\\d{3}(\\.\\d+)?")) {
            addError(ExceptionHandler.invalidDds());
        }

        if (pages <= 0) addError(ExceptionHandler.invalidPages());

        if (yearPublished == null || yearPublished.isAfter(LocalDate.now())) {
            addError(ExceptionHandler.invalidDate());
        }

        if (isNullOrBlank(type))  addError(ExceptionHandler.requiredField("Media Type"));
        if (isNullOrBlank(genre)) addError(ExceptionHandler.requiredField("Genre"));

        return errors.isEmpty();
    }

    /**
     * Lightweight check used by the GUI for adding books (title + genre + dewey only).
     * Author and pages are not collected in the current GUI so they are not validated here.
     */
    public boolean checkBookFields(String title, String genre, String dewey, String type) {
        clearErrors();
        if (isNullOrBlank(title))  addError(ExceptionHandler.requiredField("Title"));
        if (isNullOrBlank(genre))  addError(ExceptionHandler.requiredField("Genre"));
        if (isNullOrBlank(type))   addError(ExceptionHandler.requiredField("Media Type"));

        boolean needsDewey = type != null &&
                (type.equals("Book") || type.equals("ReferenceBook"));
        if (needsDewey) {
            if (isNullOrBlank(dewey)) {
                addError(ExceptionHandler.requiredField("Dewey Decimal"));
            } else if (!dewey.trim().matches("\\d{3}(\\.\\d+)?")) {
                addError(ExceptionHandler.invalidDds());
            }
        }
        return errors.isEmpty();
    }

    /**
     * Validate a numeric ID.
     */
    public boolean validateID(int id) {
        clearErrors();
        if (id <= 0) { addError(ExceptionHandler.invalidId()); return false; }
        return true;
    }

    /**
     * Validate a student ID per Mapua rules (10 digits, starts with "20").
     */
    public boolean validateStudentId(String idStr) {
        clearErrors();
        if (isNullOrBlank(idStr)) { addError(ExceptionHandler.emptyInput()); return false; }
        if (!idStr.matches("\\d{10}")) {
            addError(new ExceptionHandler(ExceptionHandler.ERR_INVALID_ID,
                    "Student ID must be exactly 10 digits.",
                    ExceptionHandler.Severity.ERROR));
            return false;
        }
        if (!idStr.startsWith("20")) {
            addError(new ExceptionHandler(ExceptionHandler.ERR_INVALID_ID,
                    "Student ID must start with '20'.",
                    ExceptionHandler.Severity.ERROR));
            return false;
        }
        return true;
    }

    // ══════════════════════════════════════════════════════════════════
    //  STORED-FIELD WORKFLOW
    // ══════════════════════════════════════════════════════════════════

    public void setInputFields(String title, String type, String genre,
                               String deweyDecimal, String isbn, String author,
                               int pages, LocalDate yearPublished) {
        this.currentTitle         = title;
        this.currentType          = type;
        this.currentGenre         = genre;
        this.currentDeweyDecimal  = deweyDecimal;
        this.currentIsbn          = isbn;
        this.currentAuthor        = author;
        this.currentPages         = pages;
        this.currentYearPublished = yearPublished;
    }
 
    public boolean validateCurrentFields() {
        return checkConstraints(currentIsbn, currentTitle, currentAuthor,
                currentDeweyDecimal, currentPages,
                currentYearPublished, currentType, currentGenre);
    }

    // ══════════════════════════════════════════════════════════════════
    //  BRIDGE METHODS  (DataValidation → ExceptionHandler)
    // ══════════════════════════════════════════════════════════════════

    /**
     * Log every collected error via ExceptionHandler.logError().
     */
    public void logAllErrors() {
        for (ExceptionHandler e : errors) e.logError();
    }

    /**
     * Returns the first user-facing message, or null if there are no errors.
     * Use this to populate the GUI status bar.
     */
    public String getFirstUserMessage() {
        return errors.isEmpty() ? null : errors.get(0).getUserMessage();
    }

    /**
     * Throws the first collected ExceptionHandler, or does nothing if valid.
     * Use in service/DB layers where you want to propagate failures.
     */
    public void throwIfInvalid() {
        if (!errors.isEmpty()) throw errors.get(0);
    }

    /**
     * Returns a copy of all collected ExceptionHandler objects.
     */
    public List<ExceptionHandler> getExceptionHandlers() {
        return Collections.unmodifiableList(new ArrayList<>(errors));
    }

    // ══════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════

    private void addError(ExceptionHandler e) { errors.add(e); }

    private static boolean isNullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public void clearErrors()               { errors.clear(); }
    public boolean isValid()                { return errors.isEmpty(); }
    public boolean hasErrors()              { return !errors.isEmpty(); }
    public int     errorCount()             { return errors.size(); }
    public boolean isYearPublishedValid(LocalDate d) { return d != null && !d.isAfter(LocalDate.now()); }
}