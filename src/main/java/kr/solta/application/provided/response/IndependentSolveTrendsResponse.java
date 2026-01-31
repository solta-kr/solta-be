package kr.solta.application.provided.response;

import java.util.List;
import kr.solta.domain.SolvedPeriod;
import kr.solta.domain.TierGroup;

public record IndependentSolveTrendsResponse(
        String period,
        String tierGroup,
        Long totalIndependentCount,
        Long totalTotalCount,
        List<IndependentRatioPoint> trends
) {

    public static IndependentSolveTrendsResponse of(
            final SolvedPeriod solvedPeriod,
            final TierGroup tierGroup,
            final List<IndependentRatioPoint> trends
    ) {
        return new IndependentSolveTrendsResponse(
                solvedPeriod.getLabel(),
                tierGroup.name(),
                trends.stream().mapToLong(IndependentRatioPoint::independentCount).sum(),
                trends.stream().mapToLong(IndependentRatioPoint::totalCount).sum(),
                trends
        );
    }
}
