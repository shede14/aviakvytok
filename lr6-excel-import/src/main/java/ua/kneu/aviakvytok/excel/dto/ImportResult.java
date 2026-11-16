package ua.kneu.aviakvytok.excel.dto;

import java.util.List;

/**
 * Результат імпорту файлу.
 *
 * @param totalRows кількість рядків з даними (без заголовка та порожніх рядків)
 * @param imported  збережено в базу (saveAll)
 * @param skipped   пропущено: помилки в даних або дублікати кодів бронювання
 */
public record ImportResult(String fileName, int totalRows, int imported, int skipped,
                           List<RowError> errors, long durationMs) {

    /** Помилка в конкретному рядку Excel (номер рядка — як у Excel, з 1). */
    public record RowError(int row, String message) {
    }
}
