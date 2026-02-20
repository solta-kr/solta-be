package kr.solta.application.required.dto;

import kr.solta.domain.Tier;

public record TierGroupStat(
        Tier tier,
        double avgSeconds,
        long count
) {
}
