package kr.solta.domain;

import lombok.Getter;

@Getter
public class TagWeakness {

    private final Tag tag;
    private final long totalCount;
    private final long selfCount;
    private final Double avgSolveSeconds;
    private final Double timeRatio;

    public TagWeakness(Tag tag, long totalCount, long selfCount, Double avgSolveSeconds, Double userOverallAvg) {
        this.tag = tag;
        this.totalCount = totalCount;
        this.selfCount = selfCount;
        this.avgSolveSeconds = avgSolveSeconds;
        this.timeRatio = computeTimeRatio(avgSolveSeconds, userOverallAvg);
    }

    public int getSelfSolveRate() {
        return (int) (selfCount * 100L / totalCount);
    }

    public double getWeaknessScore() {
        double solutionRateScore = 1.0 - (selfCount * 1.0 / totalCount);
        return solutionRateScore * 0.6 + computeTimeScore() * 0.4;
    }

    public WeaknessLevel getWeaknessLevel() {
        double score = getWeaknessScore();
        if (score >= 0.55) return WeaknessLevel.HIGH;
        if (score >= 0.30) return WeaknessLevel.MEDIUM;
        return WeaknessLevel.LOW;
    }

    public double getConfidence() {
        return Math.min(1.0, Math.log(totalCount) / Math.log(20));
    }

    public double getFinalScore() {
        return getWeaknessScore() * getConfidence();
    }

    private static Double computeTimeRatio(Double avgSolveSeconds, Double userOverallAvg) {
        if (avgSolveSeconds == null || userOverallAvg == null || userOverallAvg <= 0) return null;
        return avgSolveSeconds / userOverallAvg;
    }

    private double computeTimeScore() {
        if (timeRatio == null) return 0.5;
        return Math.min(Math.max(timeRatio - 0.5, 0.0), 1.5) / 1.5;
    }
}
