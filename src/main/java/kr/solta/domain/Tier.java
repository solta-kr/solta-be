package kr.solta.domain;

import java.util.Arrays;

public enum Tier {
    UNRATED(0),
    B5(1), B4(2), B3(3), B2(4), B1(5),
    S5(6), S4(7), S3(8), S2(9), S1(10),
    G5(11), G4(12), G3(13), G2(14), G1(15),
    P5(16), P4(17), P3(18), P2(19), P1(20),
    D5(21), D4(22), D3(23), D2(24), D1(25),
    R5(26), R4(27), R3(28), R2(29), R1(30);

    private final int level;

    Tier(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static Tier getTier(int level) {
        return Arrays.stream(Tier.values())
                .filter(tier -> tier.level == level)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid level: " + level));
    }
}
