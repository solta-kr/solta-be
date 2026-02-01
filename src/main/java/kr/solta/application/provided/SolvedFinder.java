package kr.solta.application.provided;

import java.util.List;
import java.util.Map;
import kr.solta.application.provided.request.SolvedSortType;
import kr.solta.application.provided.response.SolvedWithTags;
import kr.solta.domain.TierAverage;
import kr.solta.domain.TierGroup;
import kr.solta.domain.TierGroupAverage;

public interface SolvedFinder {

    List<TierGroupAverage> findTierGroupAverages(final String name);

    Map<TierGroup, List<TierAverage>> findTierAverages(final String name);

    List<SolvedWithTags> findSolvedWithTags(final String name);

    List<SolvedWithTags> findProblemsToRetry(final String name, final SolvedSortType sortType);
}
