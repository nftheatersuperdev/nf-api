package com.nftheater.api.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtil {

    public static final DateTimeFormatter DD_MMM_YY = DateTimeFormatter.ofPattern("dd MMM yy");
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DD_MMM_YY_THAI = DateTimeFormatter.ofPattern("dd MMM yy", new java.util.Locale("th", "TH"));
    public static final LocalTime MIN_TIME = LocalTime.of(0, 0, 0, 0);
    public static final LocalTime MAX_TIME = LocalTime.of(23, 59, 59, 999999000);

    public static ZoneId getTimeZone() {
        return ZoneId.of("Asia/Bangkok");
    }

    public static LocalDate toLocalDate(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(getTimeZone()).toLocalDate();
    }

    public static LocalTime toLocalTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(getTimeZone()).toLocalTime();
    }

    public static Date convertToDateViaInstant(LocalDate dateToConvert) {
        return Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

}
