package Library.Logic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataValidation {

    private List<String> errorList;
    private List<String> validationRules;

    // Fields for the setInputFields / validateCurrentFields workflow
    private String    currentTitle;
    private String    currentIsbn;
    private String    currentAuthor;
    private String    currentDeweyDecimal;
    private int       currentPages;
    private String    currentType;
    private String    currentGenre;
    private LocalDate currentYearPublished;

    public DataValidation() {
        errorList = new ArrayList<>();
        validationRules = new ArrayList<>();
        validationRules.add("RULE_NOT_NULL");
        validationRules.add("RULE_NOT_EMPTY");
        validationRules.add("RULE_MAX_LENGTH_200");
        validationRules.add("RULE_VALID_ISBN");
        validationRules.add("RULE_VALID_DDS");
        validationRules.add("RULE_POSITIVE_PAGES");
        validationRules.add("RULE_VALID_DATE");
        validationRules.add("RULE_POSITIVE_ID");
    }

    // ── Single field ──────────────────────────────────────────────────────
    public boolean validateInput(String data) {
        clearErrors();
        if (data == null)               { errorList.add("Input cannot be null.");                          return false; }
        if (data.trim().isEmpty())      { errorList.add("Input cannot be empty.");                         return false; }
        if (data.trim().length() > 200) { errorList.add("Input exceeds maximum length of 200 characters."); return false; }
        return true;
    }

    // ── Full constraint check ─────────────────────────────────────────────
    public boolean checkConstraints(String isbn, String title, String author,
                                    String ddsCode, int pages, LocalDate yearPublished,
                                    String type, String genre) {
        clearErrors();

        if (isbn == null || !isbn.replaceAll("-", "").trim().matches("\\d{10}|\\d{13}"))
            errorList.add("ISBN must be 10 or 13 digits.");
        if (title == null || title.trim().isEmpty())
            errorList.add("Title is required.");
        if (author == null || author.trim().isEmpty())
            errorList.add("Author is required.");
        if (ddsCode == null || !ddsCode.trim().matches("\\d{3}(\\.\\d+)?"))
            errorList.add("DDS Code format error (e.g. 005, 005.13).");
        if (pages <= 0)
            errorList.add("Pages must be > 0.");
        if (yearPublished == null || yearPublished.isAfter(LocalDate.now()))
            errorList.add("Invalid publication date.");
        if (type == null || type.trim().isEmpty())
            errorList.add("Media Type is required.");
        if (genre == null || genre.trim().isEmpty())
            errorList.add("Genre is required.");

        return errorList.isEmpty();
    }

    // ── ID check ──────────────────────────────────────────────────────────
    public boolean validateID(int id) {
        clearErrors();
        if (id <= 0) { errorList.add("ID must be a positive number."); return false; }
        return true;
    }

    // ── Stored-field workflow ─────────────────────────────────────────────
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

    // ── Helpers ───────────────────────────────────────────────────────────
    public void clearErrors() { errorList.clear(); }

    public boolean isYearPublishedValid(LocalDate publishDate) {
        return !publishDate.isAfter(LocalDate.now());
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public List<String> getErrors()          { return errorList;          }
    public String       getFirstError()      { return errorList.isEmpty() ? null : errorList.get(0); }
    public List<String> getValidationRules() { return validationRules;    }
    public boolean      isValid()            { return errorList.isEmpty(); }
}