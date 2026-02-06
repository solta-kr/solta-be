package kr.solta.application.provided;

import java.time.LocalDateTime;
import kr.solta.application.provided.request.TagKey;
import kr.solta.application.provided.response.IndependentSolveTrendsResponse;
import kr.solta.application.provided.response.SolveTimeTrendsResponse;
import kr.solta.domain.SolvedPeriod;
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
}
