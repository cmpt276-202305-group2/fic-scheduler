package com.group2.server.model;

import lombok.*;

@RequiredArgsConstructor
@AllArgsConstructor
public enum PartOfDay {
    MORNING(0b000011, Duration.FULL),
    AFTERNOON(0b001100, Duration.FULL),
    EVENING(0b110000, Duration.FULL),
    MORNING_EARLY(0b000001, Duration.HALF),
    MORNING_LATE(0b000010, Duration.HALF),
    AFTERNOON_EARLY(0b000100, Duration.HALF),
    AFTERNOON_LATE(0b001000, Duration.HALF),
    EVENING_EARLY(0b010000, Duration.HALF),
    EVENING_LATE(0b100000, Duration.HALF);

    @Getter
    private final int occupiedTimeBits;

    @Getter
    private final Duration duration;

    private PartOfDay fullPart = this;

    private PartOfDay earlyHalf = this;

    private PartOfDay lateHalf = this;

    public PartOfDay toFull() {
        return fullPart;
    }

    public PartOfDay earlyHalf() {
        return earlyHalf;
    }

    public PartOfDay lateHalf() {
        return lateHalf;
    }

    static {
        for (PartOfDay v : values()) {
            switch (v.duration) {
                case FULL:
                    for (PartOfDay h : values()) {
                        if (h.duration == Duration.HALF && conflict(v, h)) {
                            if (h.occupiedTimeBits < (v.occupiedTimeBits ^ h.occupiedTimeBits)) {
                                v.earlyHalf = h;
                            } else {
                                v.lateHalf = h;
                            }
                        }
                    }
                    break;
                case HALF:
                    for (PartOfDay f : values()) {
                        if (f.duration == Duration.FULL && conflict(v, f)) {
                            v.fullPart = f;
                        }
                    }
                    break;
            }
        }
    }

    public static PartOfDay valueOf(int occupiedTimeBits) {
        for (PartOfDay v : values()) {
            if (v.occupiedTimeBits == occupiedTimeBits) {
                return v;
            }
        }
        throw new IllegalArgumentException(String.format("Value %d not a recognized value", occupiedTimeBits));
    }

    public boolean conflict(PartOfDay other) {
        return conflict(this, other);
    }

    public static boolean conflict(PartOfDay a, PartOfDay b) {
        return (a.occupiedTimeBits & b.occupiedTimeBits) != 0;
    }
}
