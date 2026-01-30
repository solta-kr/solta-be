package kr.solta.application.provided.response;

import java.util.List;
import kr.solta.domain.SolvedPeriod;
import kr.solta.domain.TierGroup;

public record SolveTimeTrendsResponse(
        String period,
        String tierGroup,
        Long totalSolvedCount,
        List<TrendPoint> trends
) {

    public static SolveTimeTrendsResponse of(
            final SolvedPeriod solvedPeriod,
            final TierGroup tierGroup,
            final Long totalSolvedCount,
            final List<TrendPoint> trends
    ) {
        return new SolveTimeTrendsResponse(
                solvedPeriod.getLabel(),
                tierGroup.name(),
                totalSolvedCount,
                trends
        );
    }
}
