package kr.solta.application.provided;

import java.time.LocalDateTime;
import java.util.List;
import kr.solta.application.provided.request.TagKey;
import kr.solta.application.provided.response.IndependentSolveTrendsResponse;
import kr.solta.application.provided.response.SolveTimeDistributionResponse;
import kr.solta.application.provided.response.SolveTimeTrendsResponse;
import kr.solta.domain.SolvedPeriod;
import kr.solta.domain.TagWeakness;
import kr.solta.domain.TierGroup;

public interface SolvedStatisticsReader {

    SolveTimeTrendsResponse getSolveTimeTrends(
            final String name,
            final SolvedPeriod solvedPeriod,
            final TierGroup tierGroup,
            final TagKey tagKey,
            final LocalDateTime now
    );

    IndependentSolveTrendsResponse getIndependentSolveTrends(
            final String name,
            final SolvedPeriod solvedPeriod,
            final TierGroup tierGroup,
            final TagKey tagKey,
            final LocalDateTime now
    );

    SolveTimeDistributionResponse getSolveTimeDistribution(long bojProblemId, int solveTimeSeconds);

    List<TagWeakness> getTagWeakness(final String name);
}
