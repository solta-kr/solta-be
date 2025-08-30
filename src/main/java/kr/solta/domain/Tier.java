package kr.solta.domain;

import static kr.solta.domain.TierGroup.BRONZE;
import static kr.solta.domain.TierGroup.DIAMOND;
import static kr.solta.domain.TierGroup.GOLD;
import static kr.solta.domain.TierGroup.PLATINUM;
import static kr.solta.domain.TierGroup.RUBY;
import static kr.solta.domain.TierGroup.SILVER;

import java.util.Arrays;
import java.util.List;

public enum Tier {
    UNRATED(0, TierGroup.UNRATED),
    B5(1, BRONZE), B4(2, BRONZE), B3(3, BRONZE), B2(4, BRONZE), B1(5, BRONZE),
    S5(6, SILVER), S4(7, SILVER), S3(8, SILVER), S2(9, SILVER), S1(10, SILVER),
    G5(11, GOLD), G4(12, GOLD), G3(13, GOLD), G2(14, GOLD), G1(15, GOLD),
    P5(16, PLATINUM), P4(17, PLATINUM), P3(18, PLATINUM), P2(19, PLATINUM), P1(20, PLATINUM),
    D5(21, DIAMOND), D4(22, DIAMOND), D3(23, DIAMOND), D2(24, DIAMOND), D1(25, DIAMOND),
    R5(26, RUBY), R4(27, RUBY), R3(28, RUBY), R2(29, RUBY), R1(30, RUBY);

    private final int level;
    private final TierGroup group;

    Tier(int level, TierGroup group) {
        this.level = level;
        this.group = group;
    }

    public int getLevel() {
        return level;
    }

    public TierGroup getGroup() {
        return group;
    }

    public static Tier getTier(int level) {
        return Arrays.stream(Tier.values())
                .filter(tier -> tier.level == level)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid level: " + level));
    }

    public static List<Tier> getByGroup(TierGroup group) {
        return Arrays.stream(Tier.values())
                .filter(tier -> tier.group == group)
                .toList();
    }
}
