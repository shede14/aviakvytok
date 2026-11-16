package ua.kneu.aviakvytok.excel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ЛР6. Модуль аналітики продажів ТОВ «АвіаКвиток»: пакетний імпорт продажів квитків
 * з Excel (.xlsx) та статистика для побудови графіків у клієнті на Vue 3 + Chart.js.
 */
@SpringBootApplication
public class AviakvytokExcelApplication {

    public static void main(String[] args) {
        SpringApplication.run(AviakvytokExcelApplication.class, args);
    }
}
