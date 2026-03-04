package kr.solta.domain;

public enum LevelRange {
    LV_1_10  (1,   10,  0,        150,   "Newbie"),
    LV_11_30 (11,  30,  1_500,    375,   "Pupil"),
    LV_31_60 (31,  60,  9_000,    1_200, "Specialist"),
    LV_61_90 (61,  90,  45_000,   3_500, "Expert"),
    LV_91_95 (91,  95,  150_000,  8_000, "Master"),
    LV_96_99 (96,  99,  190_000, 20_000, "Master"),
    LV_100   (100, 100, 270_000,      0, "Legendary");

    private final int startLevel;
    private final int endLevel;
    private final int baseXp;      // 구간 시작 레벨에 도달하기 위한 누적 XP
    private final int xpPerLevel;  // 이 구간에서 레벨당 필요 XP
    private final String title;

    LevelRange(final int startLevel, final int endLevel, final int baseXp, final int xpPerLevel, final String title) {
        this.startLevel = startLevel;
        this.endLevel = endLevel;
        this.baseXp = baseXp;
        this.xpPerLevel = xpPerLevel;
        this.title = title;
    }

    // ---- 조회 ----

    public static LevelRange ofXp(final int totalXp) {
        LevelRange[] ranges = values();
        for (int i = ranges.length - 1; i >= 0; i--) {
            if (totalXp >= ranges[i].baseXp) return ranges[i];
        }
        return LV_1_10;
    }

    public static LevelRange ofLevel(final int level) {
        for (LevelRange range : values()) {
            if (level >= range.startLevel && level <= range.endLevel) return range;
        }
        return LV_100;  // level > 100 시에도 최상위 구간 반환
    }

    // ---- 계산 ----

    public static int calculateLevel(final int totalXp) {
        if (totalXp >= 270_000) return 100;
        LevelRange range = ofXp(totalXp);
        return range.startLevel + (totalXp - range.baseXp) / range.xpPerLevel;
    }

    public static int getLevelThreshold(final int level) {
        if (level <= 1) return 0;
        LevelRange range = ofLevel(level);
        return range.baseXp + (level - range.startLevel) * range.xpPerLevel;
    }

    public static int getXpForNextLevel(final int level) {
        if (level >= 100) return 0;
        return ofLevel(level).xpPerLevel;
    }

    public static int getCurrentLevelXp(final int totalXp, final int level) {
        return totalXp - getLevelThreshold(level);
    }

    public static int getProgressPercent(final int totalXp, final int level) {
        if (level >= 100) return 100;
        int currentLevelXp = getCurrentLevelXp(totalXp, level);
        return (int) (currentLevelXp * 100.0 / getXpForNextLevel(level));
    }

    public static String getTitle(final int level) {
        return ofLevel(level).title;
    }
}
