package com.group2.server.model;

import java.util.Calendar;

import lombok.*;

@RequiredArgsConstructor
public enum DayOfWeek {
    SUNDAY(Calendar.SUNDAY),
    MONDAY(Calendar.MONDAY),
    TUESDAY(Calendar.TUESDAY),
    WEDNESDAY(Calendar.WEDNESDAY),
    THURSDAY(Calendar.THURSDAY),
    FRIDAY(Calendar.FRIDAY),
    SATURDAY(Calendar.SATURDAY);

    public static DayOfWeek valueOf(int calendarDay) {
        if ((calendarDay < Calendar.SUNDAY) || (calendarDay > Calendar.SATURDAY)) {
            throw new IllegalArgumentException(String.format("Value %d outside valid range (%d..%d)", calendarDay,
                    Calendar.SUNDAY, Calendar.SATURDAY));
        }
        return values()[calendarDay - 1];
    }

    @Getter
    private final int calendarDay;

}
