package kr.solta.domain;

import static kr.solta.domain.Tier.B1;
import static kr.solta.domain.Tier.B2;
import static kr.solta.domain.Tier.B3;
import static kr.solta.domain.Tier.B4;
import static kr.solta.domain.Tier.B5;
import static kr.solta.domain.Tier.D1;
import static kr.solta.domain.Tier.D2;
import static kr.solta.domain.Tier.D3;
import static kr.solta.domain.Tier.D4;
import static kr.solta.domain.Tier.D5;
import static kr.solta.domain.Tier.G1;
import static kr.solta.domain.Tier.G2;
import static kr.solta.domain.Tier.G3;
import static kr.solta.domain.Tier.G4;
import static kr.solta.domain.Tier.G5;
import static kr.solta.domain.Tier.P1;
import static kr.solta.domain.Tier.P2;
import static kr.solta.domain.Tier.P3;
import static kr.solta.domain.Tier.P4;
import static kr.solta.domain.Tier.P5;
import static kr.solta.domain.Tier.R1;
import static kr.solta.domain.Tier.R2;
import static kr.solta.domain.Tier.R3;
import static kr.solta.domain.Tier.R4;
import static kr.solta.domain.Tier.R5;
import static kr.solta.domain.Tier.S1;
import static kr.solta.domain.Tier.S2;
import static kr.solta.domain.Tier.S3;
import static kr.solta.domain.Tier.S4;
import static kr.solta.domain.Tier.S5;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public enum TierGroup {
    NONE(List.of(), false, null, null),
    UNRATED(List.of(), false, null, null),
    BRONZE(List.of(B5, B4, B3, B2, B1), true, "B", "hsl(30,70%,45%)"),
    SILVER(List.of(S5, S4, S3, S2, S1), true, "S", "hsl(210,15%,60%)"),
    GOLD(List.of(G5, G4, G3, G2, G1), true, "G", "hsl(45,100%,50%)"),
    PLATINUM(List.of(P5, P4, P3, P2, P1), true, "P", "hsl(175,60%,55%)"),
    DIAMOND(List.of(D5, D4, D3, D2, D1), true, "D", "hsl(200,100%,65%)"),
    RUBY(List.of(R5, R4, R3, R2, R1), true, "R", "hsl(350,85%,55%)"),
    ;

    private final List<Tier> tiers;
    private final boolean isRated;
    private final String label;
    private final String color;

    TierGroup(final List<Tier> tiers, final boolean isRated, final String label, final String color) {
        this.tiers = tiers;
        this.isRated = isRated;
        this.label = label;
        this.color = color;
    }

    public static List<Tier> getRatedTiers() {
        return getRatedTierGroups().stream()
                .flatMap(tg -> tg.getTiers().stream())
                .toList();

    }

    public static List<TierGroup> getRatedTierGroups() {
        return Arrays.stream(TierGroup.values())
                .filter(TierGroup::isRated)
                .toList();
    }
}
