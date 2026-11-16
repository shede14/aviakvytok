package ua.kneu.aviakvytok.excel.exception;

/** Файл не можна імпортувати: порожній, не .xlsx, пошкоджений або з неправильними колонками. */
public class ExcelImportException extends RuntimeException {
    public ExcelImportException(String message) {
        super(message);
    }

    public ExcelImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
