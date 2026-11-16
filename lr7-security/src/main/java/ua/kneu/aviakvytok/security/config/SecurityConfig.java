package ua.kneu.aviakvytok.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/** Налаштування Spring Security: доступ за ролями, форма входу, вихід, обмеження сесій. */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Хешування паролів алгоритмом BCrypt (стійкість 10 за замовчуванням). */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Реєстр активних сесій: дає змогу обмежити кількість сесій і показати активних користувачів. */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * Публікує події створення та знищення HTTP-сесій у контекст Spring,
     * щоб SessionRegistry видаляв сесії після виходу або закінчення тайм-ауту.
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SessionRegistry sessionRegistry) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // загальнодоступні ресурси
                        .requestMatchers("/", "/css/**", "/favicon.svg", "/error", "/access-denied").permitAll()
                        // вхід і реєстрація — лише для неавторизованих
                        .requestMatchers("/login", "/registration").anonymous()
                        // доступ за ролями
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN", "MANAGER")
                        .requestMatchers("/manager/**").hasRole("MANAGER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")                 // власна сторінка входу
                        .defaultSuccessUrl("/user", false)
                        .failureUrl("/login?error"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout")        // повернення на головну
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")) // сторінка 403 Forbidden
                .sessionManagement(session -> session
                        .sessionFixation(fixation -> fixation.migrateSession())
                        .maximumSessions(1)                  // не більше однієї сесії на користувача
                        .maxSessionsPreventsLogin(false)     // новий вхід завершує попередню сесію
                        .sessionRegistry(sessionRegistry)
                        .expiredUrl("/login?expired"));
        return http.build();
    }
}
