package kr.solta.domain;

public record TierAverage(
        Tier tier,
        Double averageSolvedSeconds,
        long solvedCount
) {
}
