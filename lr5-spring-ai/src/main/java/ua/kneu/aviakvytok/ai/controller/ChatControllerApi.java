package ua.kneu.aviakvytok.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.UnknownContentTypeException;
import ua.kneu.aviakvytok.ai.model.GenerationResult;
import ua.kneu.aviakvytok.ai.model.SaveArticleToEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Контролер інтеграції з LLM (Spring AI, OpenAI-сумісний API).
 * Приймає неструктурований опис статті, формує запит через PromptTemplate,
 * викликає модель gpt-4o-mini і перетворює JSON-відповідь на SaveArticleToEntity.
 */
@Slf4j
@Controller
public class ChatControllerApi {

    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS");

    /**
     * Шаблон промпту. Змінна {message} — вхідний текст.
     * Фігурні дужки в самому тексті шаблону не використовуються, бо це роздільники PromptTemplate.
     */
    private static final String PROMPT = """
            Ти — сервіс перетворення JSON-даних. Твоє завдання — перетворити неструктурований
            бібліографічний опис наукової публікації на структурований запис.

            Сформуй результат строго у форматі JSON: один JSON-об'єкт, без пояснень, без коментарів,
            без markdown-розмітки і без будь-якого тексту до або після JSON.
            Усі значення — рядки (string). Якщо даних немає у тексті — порожній рядок "",
            для числових полів — "0". Не вигадуй дані, яких немає в тексті.

            Ключі об'єкта і правила для кожного з них:
            - title_of_the_article: назва статті без лапок, точно як у тексті.
            - author_set: усі автори через кому і пробіл, у тому вигляді, як у тексті (прізвище та ініціали).
            - number_of_authors: кількість авторів у author_set.
            - first_author: перший автор зі списку.
            - year_of_publication: рік видання, 4 цифри.
            - date_of_publication: повна дата у форматі ДД.ММ.РРРР, лише якщо вона є в тексті, інакше "".
            - type_of_publication: article (стаття в журналі), conference_paper (тези/матеріали конференції),
              monograph, thesis або other.
            - name_journal: назва журналу або збірника без лапок.
            - journal_volume: номер тому (лише число).
            - journal_issue: номер випуску (лише число).
            - publisher: видавництво, якщо зазначене.
            - city_of_publication: місто видання.
            - country_of_publication: країна видання; можна визначити за містом (Київ, Львів, Харків тощо — Україна).
            - conference_name: назва конференції, лише для тез.
            - first_page: перша сторінка статті (лише число).
            - last_page: остання сторінка статті (лише число).
            - total_volume_of_printed_sheets: кількість сторінок статті, обчисли за формулою
              last_page - first_page + 1. Наприклад, для «стор. 10-20» результат "11".
              Якщо вказана лише одна сторінка — "1". Якщо сторінок немає — "0".
            - doi: DOI без префікса https://doi.org/.
            - issn: ISSN, якщо є.
            - isbn: ISBN, якщо є.
            - udc: УДК, якщо є.
            - url: посилання, якщо є в тексті (DOI сюди не переносити).
            - language: мова назви статті: "ukr" — українська, "eng" — англійська, "other" — будь-яка інша.
            - published_in_ukraine: "1", якщо місто або країна видання — Україна, інакше "0".
            - published_abroad: "1", якщо видання за кордоном, інакше "0".
              published_in_ukraine і published_abroad не можуть одночасно бути "1".
              Якщо країну визначити неможливо — обидва "0".
            - specialized: завжди "Category A".
            - scopus: "1", лише якщо в тексті прямо сказано про індексацію в Scopus, інакше "0".
            - web_of_science: "1", лише якщо в тексті прямо сказано про Web of Science, інакше "0".
            - keywords: ключові слова через кому, лише якщо вони наведені в тексті.
            - number_of_references: кількість джерел у списку літератури, лише якщо вказана в тексті, інакше "0".
            - funding: грант або джерело фінансування, лише якщо вказане.
            - bibliographic_description: бібліографічний опис за ДСТУ 8302:2015, складений лише з наявних даних.
            - notes: коротко українською мовою (до 15 слів) — яких важливих даних бракує у вхідному тексті;
              якщо всього вистачає — "".

            Вхідний текст:
            {message}
            """;

    private final OpenAiChatModel chatModel;
    private final ObjectMapper objectMapper;
    private final String modelName;
    private final boolean apiKeyMissing;

    public ChatControllerApi(OpenAiChatModel chatModel,
                             ObjectMapper objectMapper,
                             @Value("${spring.ai.openai.chat.options.model}") String modelName,
                             @Value("${spring.ai.openai.api-key}") String apiKey) {
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
        this.modelName = modelName;
        this.apiKeyMissing = apiKey == null || apiKey.isBlank() || "no-key".equals(apiKey);
    }

    /** Сторінка з формою введення тексту статті. */
    @GetMapping("/")
    public String index(Model model) {
        fillCommon(model, "");
        return "index";
    }

    /** Обробка форми: генерація структурованого запису і вивід результату на сторінку. */
    @PostMapping("/generate-all")
    public String generateAll(@RequestParam("message") String message, Model model) {
        GenerationResult result = generate(message);
        fillCommon(model, message);
        model.addAttribute("result", result);
        return "index";
    }

    /** Той самий сервіс у вигляді REST API (для Postman / інших клієнтів). */
    @PostMapping(value = "/api/generate-all",
            consumes = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public GenerationResult generateAllApi(@RequestBody String message) {
        return generate(message);
    }

    // ------------------------------------------------------------------

    private GenerationResult generate(String message) {
        LocalDateTime started = LocalDateTime.now();
        long t0 = System.nanoTime();

        if (message == null || message.isBlank()) {
            return GenerationResult.failure("Введіть текст статті (бібліографічний опис).",
                    "", started, started, 0, modelName);
        }

        // 1. Формуємо запит через PromptTemplate
        PromptTemplate template = new PromptTemplate(PROMPT);
        Prompt prompt = template.create(Map.of("message", message.trim()));

        // 2. Викликаємо модель
        String raw;
        try {
            ChatResponse response = chatModel.call(prompt);
            raw = response.getResult().getOutput().getText();
        } catch (RuntimeException e) {
            log.error("Помилка звернення до моделі", e);
            String hint = apiKeyMissing
                    ? "Не задано змінну середовища OPENAI_API_KEY. "
                    : "";
            return GenerationResult.failure(hint + "Модель недоступна: " + shortMessage(e),
                    "", started, LocalDateTime.now(), elapsedMs(t0), modelName);
        }
        log.debug("Відповідь моделі:\n{}", raw);

        // 3. Очищаємо відповідь від markdown-маркерів ```json ... ```
        String json = cleanJson(raw);

        // 4. Десеріалізація JSON -> Java Record (Jackson)
        try {
            SaveArticleToEntity article = objectMapper.readValue(json, SaveArticleToEntity.class);
            String pretty = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(objectMapper.readTree(json));
            return GenerationResult.success(article, raw, pretty, started, LocalDateTime.now(),
                    elapsedMs(t0), modelName);
        } catch (IOException e) {
            log.warn("Модель повернула некоректний JSON: {}", e.getMessage());
            return GenerationResult.failure("Модель повернула некоректний JSON: " + shortMessage(e),
                    raw, started, LocalDateTime.now(), elapsedMs(t0), modelName);
        }
    }

    /** Прибирає ```json / ``` та будь-який текст поза межами JSON-об'єкта. */
    static String cleanJson(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim()
                .replaceAll("(?i)^```\\s*json\\s*", "")
                .replaceAll("^```\\s*", "")
                .replaceAll("\\s*```$", "")
                .trim();
        int start = s.indexOf('{');
        int end = s.lastIndexOf('}');
        return (start >= 0 && end > start) ? s.substring(start, end + 1) : s;
    }

    private void fillCommon(Model model, String message) {
        model.addAttribute("message", message);
        model.addAttribute("modelName", modelName);
        model.addAttribute("apiKeyMissing", apiKeyMissing);
        model.addAttribute("now", LocalDateTime.now().format(CLOCK));
    }

    private static long elapsedMs(long t0) {
        return (System.nanoTime() - t0) / 1_000_000;
    }

    private static String shortMessage(Throwable e) {
        String m = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof RestClientResponseException r) {
                // HTTP-статус і тіло відповіді сервера моделі (наприклад, 401 Unauthorized, 429 Too Many Requests)
                m = "HTTP " + r.getStatusCode().value() + " — " + r.getResponseBodyAsString();
                break;
            }
            if (t instanceof UnknownContentTypeException u) {
                // відповідь прийшла не у форматі JSON (наприклад, text/plain)
                m = "HTTP " + u.getStatusCode().value() + ", " + u.getContentType() + " — " + u.getResponseBodyAsString();
                break;
            }
        }
        m = m.replaceAll("\\s+", " ");
        return m.length() > 300 ? m.substring(0, 300) + "…" : m;
    }
}
