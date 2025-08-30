package kr.solta.adapter.webapi.response;

import kr.solta.domain.Tier;

public record TierAverageTimeResponse(
        Tier tier,
        Double averageSolvedSeconds,
        Long solvedCount
) {
}
