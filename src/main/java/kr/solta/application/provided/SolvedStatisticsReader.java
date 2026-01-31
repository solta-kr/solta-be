package kr.solta.application.provided;

import kr.solta.application.provided.response.SolveTimeTrendsResponse;
import kr.solta.domain.SolvedPeriod;
import kr.solta.domain.TierGroup;

public interface SolvedStatisticsReader {

    SolveTimeTrendsResponse getSolveTimeTrends(
            final String name,
            final SolvedPeriod solvedPeriod,
            final TierGroup tierGroup
    );
}
