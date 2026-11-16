# ЛР5. AI-модуль «АвіаКвиток»: Spring AI + gpt-4o-mini

Окремий застосунок на **Spring Boot 3.5** (нереактивний стек, Spring MVC + Thymeleaf), який за
допомогою **Spring AI** перетворює неструктурований бібліографічний опис статті на JSON і
десеріалізує його в Java Record `SaveArticleToEntity` (Jackson).

| Компонент | Призначення |
|---|---|
| `model/SaveArticleToEntity` | Record на 33 поля, `@JsonIgnoreProperties(ignoreUnknown = true)`, компактний конструктор замінює `null` на `""` / `"0"` |
| `controller/ChatControllerApi` | `OpenAiChatModel` через конструктор, `PromptTemplate` з `{message}`, `chatModel.call(prompt)`, очищення від `` ```json ``, `ObjectMapper.readValue` з обробкою `IOException` |
| `config/HttpClientConfig` | HTTP-клієнт для Spring AI: HTTP/1.1, тайм-аути, журналювання запитів до моделі |
| `model/GenerationResult` | результат + час генерації (clock) + текст помилки |
| `templates/index.html` | форма введення, час генерації, таблиця полів, JSON-відповідь |

Ендпоінти: `GET /` — форма, `POST /generate-all` — обробка форми, `POST /api/generate-all` — REST (text/plain → JSON).

## Ключ API і провайдер

Ключ читається зі змінної середовища `OPENAI_API_KEY` (`spring.ai.openai.api-key=${OPENAI_API_KEY}`), у коді та в Git його немає.

Spring AI OpenAI працює з будь-яким OpenAI-сумісним API, тому за замовчуванням використовується безкоштовний
тариф **Groq** (`https://api.groq.com/openai`, модель `openai/gpt-oss-120b` — відкрита модель OpenAI).
Ключ — https://console.groq.com/keys.

```bat
setx OPENAI_API_KEY "gsk_..."
```

Щоб працювати з оригінальним OpenAI `gpt-4o-mini` (платний API), достатньо задати ще дві змінні середовища:
`OPENAI_BASE_URL=https://api.openai.com`, `OPENAI_MODEL=gpt-4o-mini` — код не змінюється.

## Запуск

```bash
cd lr5-spring-ai
mvn spring-boot:run      # http://localhost:8081
```
