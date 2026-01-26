package kr.solta.domain;

public record TierAverage(
        Tier tier,
        Double averageSolvedSeconds,
        long solvedCount
) {

    public static TierAverage none(Tier tier) {
        return new TierAverage(tier, null, 0);
    }
}
