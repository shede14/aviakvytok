package ua.kneu.aviakvytok.excel.entity;

import java.util.Locale;

/** Клас обслуговування. */
public enum FareClass {
    ECONOMY("Економ"),
    BUSINESS("Бізнес"),
    FIRST("Перший");

    private final String label;

    FareClass(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Розпізнає значення з Excel: українська назва або код (Економ / ECONOMY / economy). */
    public static FareClass parse(String value) {
        String v = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        for (FareClass c : values()) {
            if (c.label.toLowerCase(Locale.ROOT).equals(v) || c.name().toLowerCase(Locale.ROOT).equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException("невідомий клас обслуговування «" + value + "» (Економ, Бізнес, Перший)");
    }
}
