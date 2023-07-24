package com.group2.server.model;

import lombok.*;

@RequiredArgsConstructor
public enum PartOfDay {
    MORNING(0b000011, true),
    AFTERNOON(0b001100, true),
    EVENING(0b110000, true),
    MORNING_EARLY(0b000001, false),
    MORNING_LATE(0b000010, false),
    AFTERNOON_EARLY(0b000100, false),
    AFTERNOON_LATE(0b001000, false),
    EVENING_EARLY(0b010000, false),
    EVENING_LATE(0b100000, false);

    @Getter
    private final int occupiedTimeBits;

    @Getter
    private final boolean fullPeriod;

    public boolean isHalfPeriod() {
        return !fullPeriod;
    }
}
