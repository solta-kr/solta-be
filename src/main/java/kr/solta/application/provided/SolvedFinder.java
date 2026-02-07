package kr.solta.application.provided;

import java.util.List;
import java.util.Map;
import kr.solta.application.provided.request.SolvedSortType;
import kr.solta.application.provided.request.TagKey;
import kr.solta.application.provided.response.SolvedWithTags;
import kr.solta.domain.TierAverage;
import kr.solta.domain.TierGroup;
import kr.solta.domain.TierGroupAverage;

public interface SolvedFinder {

    List<TierGroupAverage> findTierGroupAverages(final String name, final TagKey tagKey);

    Map<TierGroup, List<TierAverage>> findTierAverages(final String name, final TagKey tagKey);

    List<SolvedWithTags> findSolvedWithTags(final String name);

    List<SolvedWithTags> findProblemsToRetry(final String name, final SolvedSortType sortType);
}
