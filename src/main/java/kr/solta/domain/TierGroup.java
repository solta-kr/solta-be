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

import java.util.List;
import lombok.Getter;

@Getter
public enum TierGroup {
    UNRATED(List.of()),
    BRONZE(List.of(B5, B4, B3, B2, B1)),
    SILVER(List.of(S5, S4, S3, S2, S1)),
    GOLD(List.of(G5, G4, G3, G2, G1)),
    PLATINUM(List.of(P5, P4, P3, P2, P1)),
    DIAMOND(List.of(D5, D4, D3, D2, D1)),
    RUBY(List.of(R5, R4, R3, R2, R1)),
    ;

    private final List<Tier> tiers;

    TierGroup(List<Tier> tiers) {
        this.tiers = tiers;
    }
}
