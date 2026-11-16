package ua.kneu.aviakvytok.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Налаштування HTTP-клієнта, яким Spring AI звертається до OpenAI-сумісного API:
 * HTTP/1.1, тайм-аути з'єднання та читання, буферизація тіла запиту (заголовок Content-Length)
 * і журналювання кожного звернення до моделі (метод, URL, HTTP-статус, тип вмісту).
 */
@Slf4j
@Configuration
public class HttpClientConfig {

    @Bean
    public RestClientCustomizer aiRestClientCustomizer() {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        JdkClientHttpRequestFactory jdk = new JdkClientHttpRequestFactory(httpClient);
        jdk.setReadTimeout(Duration.ofSeconds(60));
        return builder -> builder
                .requestFactory(new BufferingClientHttpRequestFactory(jdk))
                .requestInterceptor((request, body, execution) -> {
                    var response = execution.execute(request, body);
                    log.info("AI HTTP {} {} -> {} {}", request.getMethod(), request.getURI(),
                            response.getStatusCode().value(), response.getHeaders().getContentType());
                    return response;
                });
    }
}
