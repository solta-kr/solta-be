package kr.solta.domain;

import java.math.BigDecimal;

public enum StreakBonus {
    NONE  (0,  6,                 BigDecimal.ZERO),
    WEEK  (7,  13,                new BigDecimal("0.10")),
    BIWEEK(14, 29,                new BigDecimal("0.20")),
    MONTH (30, Integer.MAX_VALUE, new BigDecimal("0.30"));

    private final int minStreak;
    private final int maxStreak;
    private final BigDecimal multiplier;

    StreakBonus(final int minStreak, final int maxStreak, final BigDecimal multiplier) {
        this.minStreak = minStreak;
        this.maxStreak = maxStreak;
        this.multiplier = multiplier;
    }

    public static BigDecimal of(final int streak) {
        for (StreakBonus bonus : values()) {
            if (streak >= bonus.minStreak && streak <= bonus.maxStreak) return bonus.multiplier;
        }
        return BigDecimal.ZERO;
    }
}
