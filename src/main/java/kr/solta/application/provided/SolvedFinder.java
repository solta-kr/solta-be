package kr.solta.application.provided;

import java.util.List;
import java.util.Map;
import kr.solta.application.provided.response.SolvedWithTags;
import kr.solta.domain.TierAverage;
import kr.solta.domain.TierGroup;
import kr.solta.domain.TierGroupAverage;

public interface SolvedFinder {

    List<TierGroupAverage> findTierGroupAverages(String name);

    Map<TierGroup, List<TierAverage>> findTierAverages(String name);

    List<SolvedWithTags> findSolvedWithTags(String name);
}
