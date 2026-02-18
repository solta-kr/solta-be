package kr.solta.application.provided.response;

import java.util.List;
import kr.solta.domain.Tier;

public record SolveTimeDistributionResponse(
        long bojProblemId,
        String title,
        Tier tier,
        long totalSolverCount,
        int bucketSize,
        List<DistributionBucket> distribution,
        MyPosition myPosition
) {

    public record DistributionBucket(int rangeStart, int rangeEnd, long count) {
    }

    public record MyPosition(int solveTimeSeconds, double topPercent) {
    }
}
