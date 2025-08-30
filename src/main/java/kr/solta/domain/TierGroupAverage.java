package kr.solta.domain;

public record TierGroupAverage(
        TierGroup tierGroup,
        Double averageSolvedSeconds
) {
}
