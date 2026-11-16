package ua.kneu.aviakvytok.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ua.kneu.aviakvytok.ai.model.SaveArticleToEntity;

import static org.junit.jupiter.api.Assertions.*;

/** Модульні тести обробки відповіді LLM (без звернення до моделі). */
class JsonParsingTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void cleansMarkdownFence() {
        String raw = "```json\n{\"title_of_the_article\":\"Тест\"}\n```";
        assertEquals("{\"title_of_the_article\":\"Тест\"}", ChatControllerApi.cleanJson(raw));
    }

    @Test
    void cleansTextAroundJson() {
        String raw = "Ось результат: {\"language\":\"ukr\"} Дякую!";
        assertEquals("{\"language\":\"ukr\"}", ChatControllerApi.cleanJson(raw));
    }

    @Test
    void nullsAndMissingFieldsBecomeEmptyOrZero() throws Exception {
        String json = "{\"title_of_the_article\":null,\"first_page\":\"10\",\"last_page\":\"20\","
                + "\"total_volume_of_printed_sheets\":\"11\",\"unknown_field\":\"x\"}";
        SaveArticleToEntity a = mapper.readValue(json, SaveArticleToEntity.class);
        assertEquals("", a.title_of_the_article());   // null -> ""
        assertEquals("", a.doi());                    // відсутнє -> ""
        assertEquals("0", a.year_of_publication());   // відсутнє числове -> "0"
        assertEquals("11", a.pagesCheckedByJava());
        assertTrue(a.pagesMatch());
    }

    @Test
    void numbersFromModelAreAcceptedAsStrings() throws Exception {
        SaveArticleToEntity a = mapper.readValue("{\"year_of_publication\":2025}", SaveArticleToEntity.class);
        assertEquals("2025", a.year_of_publication());
    }

    @Test
    void emptyObjectHasNoNulls() {
        SaveArticleToEntity e = SaveArticleToEntity.empty();
        e.asMap().values().forEach(v -> assertNotNull(v));
        assertEquals("0", e.pagesCheckedByJava());   // сторінок немає -> 0
        assertTrue(e.pagesMatch());
    }

    @Test
    void invalidJsonThrowsIOException() {
        assertThrows(java.io.IOException.class,
                () -> mapper.readValue("це не JSON", SaveArticleToEntity.class));
    }
}
