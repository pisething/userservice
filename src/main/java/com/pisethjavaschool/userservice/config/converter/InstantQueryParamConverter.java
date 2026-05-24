package com.pisethjavaschool.userservice.config.converter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.core.convert.converter.Converter;

public class InstantQueryParamConverter implements Converter<String, Instant> {

    private final ZoneId defaultZone;

    public InstantQueryParamConverter(ZoneId defaultZone) {
        this.defaultZone = defaultZone;
    }

    @Override
    public Instant convert(String source) {
        if (source == null) {
            return null;
        }
        String s = source.trim();
        if (s.isEmpty()) {
            return null;
        }

        // 1) Full Instant: 2026-01-14T10:20:30Z
        try {
            return Instant.parse(s);
        } catch (DateTimeParseException ignored) {
            // try next
        }

        // 2) Offset date-time: 2026-01-14T10:20:30+07:00
        try {
            return OffsetDateTime.parse(s).toInstant();
        } catch (DateTimeParseException ignored) {
            // try next
        }

        // 3) Local date-time: 2026-01-14T10:20:30  (assume default zone)
        try {
            LocalDateTime ldt = LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return ldt.atZone(defaultZone).toInstant();
        } catch (DateTimeParseException ignored) {
            // try next
        }

        // 4) Date only: 2026-01-14  (default start of day in default zone)
        try {
            LocalDate d = LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
            return d.atStartOfDay(defaultZone).toInstant();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date/time value: " + source);
        }
    }
}
