package ua.kneu.aviakvytok.excel.entity;

import java.util.Locale;

/** Статус продажу квитка. */
public enum SaleStatus {
    PAID("Оплачено"),
    REFUNDED("Повернено"),
    CANCELLED("Скасовано");

    private final String label;

    SaleStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Розпізнає значення з Excel: українська назва або код (Оплачено / PAID). */
    public static SaleStatus parse(String value) {
        String v = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        for (SaleStatus s : values()) {
            if (s.label.toLowerCase(Locale.ROOT).equals(v) || s.name().toLowerCase(Locale.ROOT).equals(v)) {
                return s;
            }
        }
        throw new IllegalArgumentException("невідомий статус «" + value + "» (Оплачено, Повернено, Скасовано)");
    }
}
