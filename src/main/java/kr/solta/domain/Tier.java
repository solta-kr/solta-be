package kr.solta.domain;

import java.util.Arrays;

public enum Tier {
    UNRATED(0,  0.0),
    B5(1,  0.5), B4(2,  0.6), B3(3,  0.7), B2(4,  0.8), B1(5,  0.9),
    S5(6,  1.0), S4(7,  1.2), S3(8,  1.4), S2(9,  1.6), S1(10, 1.8),
    G5(11, 2.0), G4(12, 2.3), G3(13, 2.6), G2(14, 3.0), G1(15, 3.5),
    P5(16, 4.0), P4(17, 4.5), P3(18, 5.0), P2(19, 5.5), P1(20, 6.0),
    D5(21, 6.5), D4(22, 7.0), D3(23, 7.5), D2(24, 8.0), D1(25, 8.5),
    R5(26, 9.0), R4(27, 9.5), R3(28, 10.0), R2(29, 10.5), R1(30, 11.0);

    private final int level;
    private final double xpWeight;

    Tier(final int level, final double xpWeight) {
        this.level = level;
        this.xpWeight = xpWeight;
    }

    public int getLevel() {
        return level;
    }

    public double getXpWeight() {
        return xpWeight;
    }

    public static Tier getTier(final int level) {
        return Arrays.stream(Tier.values())
                .filter(tier -> tier.level == level)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid level: " + level));
    }
}
