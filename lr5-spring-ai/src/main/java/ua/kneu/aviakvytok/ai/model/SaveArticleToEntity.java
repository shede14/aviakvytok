package ua.kneu.aviakvytok.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Структурований запис про наукову публікацію (статтю), отриманий від LLM.
 * <p>
 * Імена компонентів збігаються з ключами JSON, які повертає модель (snake_case),
 * тому Jackson десеріалізує відповідь без додаткових анотацій. Невідомі ключі
 * ігноруються ({@code ignoreUnknown = true}), а компактний конструктор замінює
 * {@code null} на порожній рядок (текстові поля) або на "0" (числові та логічні
 * поля) — завдяки цьому в шаблоні та сервісах не виникає NullPointerException.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SaveArticleToEntity(
        // --- основні бібліографічні дані ---
        String title_of_the_article,          // назва статті
        String author_set,                    // автори через кому, як у тексті
        String number_of_authors,             // кількість авторів (число)
        String first_author,                  // перший автор
        String year_of_publication,           // рік (число)
        String date_of_publication,           // повна дата, якщо є у тексті
        String type_of_publication,           // article / conference_paper / thesis / monograph / other
        // --- видання ---
        String name_journal,                  // назва журналу / збірника
        String journal_volume,                // том
        String journal_issue,                 // випуск / номер
        String publisher,                     // видавництво
        String city_of_publication,           // місто
        String country_of_publication,        // країна
        String conference_name,               // назва конференції (для тез)
        // --- сторінки ---
        String first_page,                    // перша сторінка (число)
        String last_page,                     // остання сторінка (число)
        String total_volume_of_printed_sheets,// кількість сторінок = last - first + 1
        // --- ідентифікатори ---
        String doi,
        String issn,
        String isbn,
        String udc,                           // УДК
        String url,
        // --- мова та географія ---
        String language,                      // ukr / eng / other
        String published_in_ukraine,          // "1" / "0"
        String published_abroad,              // "1" / "0"
        // --- індексація та категорія ---
        String specialized,                   // фахове видання — завжди "Category A"
        String scopus,                        // "1" / "0"
        String web_of_science,                // "1" / "0"
        // --- додатково ---
        String keywords,                      // ключові слова, якщо вони є в тексті
        String number_of_references,          // кількість джерел у списку літератури (число)
        String funding,                       // грант / фінансування, якщо зазначено
        String bibliographic_description,     // бібліографічний опис (ДСТУ 8302:2015)
        String notes                          // примітки моделі (чого бракує у вхідних даних)
) {

    /** Компактний конструктор: захист від null. */
    public SaveArticleToEntity {
        title_of_the_article = text(title_of_the_article);
        author_set = text(author_set);
        number_of_authors = number(number_of_authors);
        first_author = text(first_author);
        year_of_publication = number(year_of_publication);
        date_of_publication = text(date_of_publication);
        type_of_publication = text(type_of_publication);
        name_journal = text(name_journal);
        journal_volume = text(journal_volume);
        journal_issue = text(journal_issue);
        publisher = text(publisher);
        city_of_publication = text(city_of_publication);
        country_of_publication = text(country_of_publication);
        conference_name = text(conference_name);
        first_page = number(first_page);
        last_page = number(last_page);
        total_volume_of_printed_sheets = number(total_volume_of_printed_sheets);
        doi = text(doi);
        issn = text(issn);
        isbn = text(isbn);
        udc = text(udc);
        url = text(url);
        language = text(language);
        published_in_ukraine = number(published_in_ukraine);
        published_abroad = number(published_abroad);
        specialized = text(specialized);
        scopus = number(scopus);
        web_of_science = number(web_of_science);
        keywords = text(keywords);
        number_of_references = number(number_of_references);
        funding = text(funding);
        bibliographic_description = text(bibliographic_description);
        notes = text(notes);
    }

    private static String text(String value) {
        return value == null || value.equalsIgnoreCase("null") ? "" : value.trim();
    }

    private static String number(String value) {
        String v = text(value);
        return v.isEmpty() ? "0" : v;
    }

    /** Порожній об'єкт — повертається, якщо модель видала некоректний JSON. */
    public static SaveArticleToEntity empty() {
        return new SaveArticleToEntity(null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null);
    }

    /** Контрольне обчислення кількості сторінок на боці Java (для перевірки відповіді LLM). */
    public String pagesCheckedByJava() {
        try {
            int first = Integer.parseInt(first_page.replaceAll("\\D", ""));
            int last = Integer.parseInt(last_page.replaceAll("\\D", ""));
            if (first == 0 && last == 0) {
                return "0";                        // сторінок у тексті немає
            }
            if (first > 0 && last == 0) {
                return "1";                        // вказана лише одна сторінка
            }
            return first > 0 && last >= first ? String.valueOf(last - first + 1) : "—";
        } catch (NumberFormatException e) {
            return "—";
        }
    }

    /** Чи збігається кількість сторінок, обчислена LLM, з контрольним обчисленням Java. */
    public boolean pagesMatch() {
        return pagesCheckedByJava().equals(total_volume_of_printed_sheets);
    }

    /** Усі поля у вигляді «ключ → значення» (для таблиці на сторінці). */
    public Map<String, String> asMap() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("title_of_the_article", title_of_the_article);
        m.put("author_set", author_set);
        m.put("number_of_authors", number_of_authors);
        m.put("first_author", first_author);
        m.put("year_of_publication", year_of_publication);
        m.put("date_of_publication", date_of_publication);
        m.put("type_of_publication", type_of_publication);
        m.put("name_journal", name_journal);
        m.put("journal_volume", journal_volume);
        m.put("journal_issue", journal_issue);
        m.put("publisher", publisher);
        m.put("city_of_publication", city_of_publication);
        m.put("country_of_publication", country_of_publication);
        m.put("conference_name", conference_name);
        m.put("first_page", first_page);
        m.put("last_page", last_page);
        m.put("total_volume_of_printed_sheets", total_volume_of_printed_sheets);
        m.put("doi", doi);
        m.put("issn", issn);
        m.put("isbn", isbn);
        m.put("udc", udc);
        m.put("url", url);
        m.put("language", language);
        m.put("published_in_ukraine", published_in_ukraine);
        m.put("published_abroad", published_abroad);
        m.put("specialized", specialized);
        m.put("scopus", scopus);
        m.put("web_of_science", web_of_science);
        m.put("keywords", keywords);
        m.put("number_of_references", number_of_references);
        m.put("funding", funding);
        m.put("bibliographic_description", bibliographic_description);
        m.put("notes", notes);
        return m;
    }
}
