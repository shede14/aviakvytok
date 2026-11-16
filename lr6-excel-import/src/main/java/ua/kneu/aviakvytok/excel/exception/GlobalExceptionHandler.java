package ua.kneu.aviakvytok.excel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Map;

/** Перетворює помилки імпорту на JSON-відповіді з повідомленням українською. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExcelImportException.class)
    public ResponseEntity<Map<String, String>> importError(ExcelImportException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Map<String, String>> noFile() {
        return ResponseEntity.badRequest().body(Map.of("message", "Файл не передано (очікується поле form-data «file»)."));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> tooLarge() {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Map.of("message", "Файл завеликий: максимальний розмір — 5 МБ."));
    }
}
