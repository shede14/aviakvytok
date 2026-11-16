package ua.kneu.aviakvytok.excel.dto;

/** Один стовпчик / сектор графіка: мітка та кількість записів. */
public record ChartItem(String label, long count) {
}
