package ua.kneu.aviakvytok.ai.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Результат одного звернення до LLM: структурований запис, «сира» відповідь моделі,
 * очищений JSON, повідомлення про помилку та час генерації (clock).
 */
public record GenerationResult(
        boolean ok,
        SaveArticleToEntity article,
        String rawResponse,
        String cleanedJson,
        String error,
        String startedAt,
        String finishedAt,
        long durationMs,
        String model
) {
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static GenerationResult success(SaveArticleToEntity article, String raw, String json,
                                           LocalDateTime started, LocalDateTime finished,
                                           long durationMs, String model) {
        return new GenerationResult(true, article, raw, json, "",
                started.format(CLOCK), finished.format(CLOCK), durationMs, model);
    }

    /** У разі помилки повертається порожній об'єкт SaveArticleToEntity (без null-полів). */
    public static GenerationResult failure(String error, String raw,
                                           LocalDateTime started, LocalDateTime finished,
                                           long durationMs, String model) {
        return new GenerationResult(false, SaveArticleToEntity.empty(), raw == null ? "" : raw, "", error,
                started.format(CLOCK), finished.format(CLOCK), durationMs, model);
    }

    /** Тривалість у секундах з двома знаками після коми. */
    public String durationSeconds() {
        return String.format("%.2f", durationMs / 1000.0).replace('.', ',');
    }
}
