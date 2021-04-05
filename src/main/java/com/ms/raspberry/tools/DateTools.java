package com.ms.raspberry.tools;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateTools {
    public static LocalDate stringToDate(String dateStr) {
        try {
            return dateStr != null ? LocalDate.parse(dateStr) : null;
        } catch (DateTimeParseException exc) {
            return null;
        }
    }
}
