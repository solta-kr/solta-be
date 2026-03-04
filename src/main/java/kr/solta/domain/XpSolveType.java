package kr.solta.domain;

public enum XpSolveType {
    SELF           (1.5,  true),
    SOLUTION       (15.0, false),
    REVIEW_SELF    (2.0,  true),
    REVIEW_SOLUTION(18.0, false);

    private final double multiplier;
    private final boolean usesTime;

    XpSolveType(final double multiplier, final boolean usesTime) {
        this.multiplier = multiplier;
        this.usesTime = usesTime;
    }

    public int calculateBaseXp(final Tier tier, final Integer solveTimeSeconds) {
        if (!usesTime || solveTimeSeconds == null || solveTimeSeconds == 0) {
            return (int) Math.round(tier.getXpWeight() * multiplier);
        }
        double effectiveMinutes = calculateEffectiveMinutes(solveTimeSeconds);
        return (int) Math.round(effectiveMinutes * tier.getXpWeight() * multiplier);
    }

    // 60분 초과분은 절반 효율로 처리 — 장시간 방치 어뷰징 완화
    // 240분(4시간)으로 cap 후: t ≤ 60 → t, t > 60 → 60 + (t - 60) * 0.5
    private double calculateEffectiveMinutes(final int solveTimeSeconds) {
        double minutes = Math.min(solveTimeSeconds / 60.0, 240.0);
        return minutes <= 60 ? minutes : 60 + (minutes - 60) * 0.5;
    }

    public boolean isReview() {
        return this == REVIEW_SELF || this == REVIEW_SOLUTION;
    }

    public boolean isSolution() {
        return this == SOLUTION || this == REVIEW_SOLUTION;
    }

    public static XpSolveType from(final SolveType solveType, final boolean isReview) {
        if (isReview) {
            return solveType == SolveType.SELF ? REVIEW_SELF : REVIEW_SOLUTION;
        }
        return solveType == SolveType.SELF ? SELF : SOLUTION;
    }
}
