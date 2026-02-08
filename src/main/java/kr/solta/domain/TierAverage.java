package kr.solta.domain;

public record TierAverage(
        Tier tier,
        Double averageSolvedSeconds,
        long solvedCount,
        long independentCount
) {

    public static TierAverage none(Tier tier) {
        return new TierAverage(tier, null, 0, 0);
    }
}
