package Library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataValidation {

    private List<String> errorList;
    private List<String> validationRules;

    // Constructor
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

    // validaInput
    public boolean validateInput(String data) {
        clearErrors();

        if (data == null) {
            errorList.add("Input cannot be null.");
            return false;
        }

        if (data.trim().isEmpty()) {
            errorList.add("Input cannot be empty.");
            return false;
        }

        if (data.trim().length() > 200) {
            errorList.add("Input exceeds maximum length of 200 characters.");
            return false;
        }

        return true;
    }

    // checkConstraints
    public boolean checkConstraints(String isbn, String title, String author,
                                    String ddsCode, int pages, LocalDate yearPublished) {
        clearErrors();

        // ISBN 
        if (isbn == null || isbn.trim().isEmpty()) {
            errorList.add("ISBN is required.");
        } else {
            String isbnDigits = isbn.replaceAll("-", "").trim();
            if (!isbnDigits.matches("\\d{10}|\\d{13}")) {
                errorList.add("ISBN must be exactly 10 or 13 digits.");
            }
        }

        // Title, max 200 chars
        if (title == null || title.trim().isEmpty()) {
            errorList.add("Title is required.");
        } else if (title.trim().length() > 200) {
            errorList.add("Title exceeds 200 characters.");
        }

        // Author, max 200 chars
        if (author == null || author.trim().isEmpty()) {
            errorList.add("Author is required.");
        } else if (author.trim().length() > 200) {
            errorList.add("Author exceeds 200 characters.");
        }

        // DDS Code
        if (ddsCode == null || ddsCode.trim().isEmpty()) {
            errorList.add("DDS Code is required.");
        } else if (!ddsCode.trim().matches("\\d{3}(\\.\\d+)?")) {
            errorList.add("DDS Code must follow Dewey Decimal format (e.g. 005, 005.13).");
        }

        // Pages must be positive
        if (pages <= 0) {
            errorList.add("Pages must be a positive number greater than 0.");
        }

        // Year Published 
        if (yearPublished == null) {
            errorList.add("Year Published is required.");
        } else if (!isYearPublishedValid(yearPublished)) {
            errorList.add("Year Published cannot be a future date.");
        }

        return errorList.isEmpty();
    }

    // validateID
    public boolean validateID(int id) {
        clearErrors();

        if (id <= 0) {
            errorList.add("ID must be a positive number.");
            return false;
        }

        return true;
    }

    // clearErrors
    public void clearErrors() {
        errorList.clear();
    }

    // Getters
    public List<String> getErrors() {
        return errorList;
    }

    public List<String> getValidationRules() {
        return validationRules;
    }

    public boolean isValid() {
        return errorList.isEmpty();
    }

    public boolean isYearPublishedValid(LocalDate publishDate) {
        return !publishDate.isAfter(LocalDate.now());
    }
}