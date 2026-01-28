package kr.solta.application.provided;

import java.util.List;
import java.util.Map;
import kr.solta.application.provided.response.SolvedWithTags;
import kr.solta.domain.TierAverage;
import kr.solta.domain.TierGroup;
import kr.solta.domain.TierGroupAverage;

public interface SolvedFinder {

    List<TierGroupAverage> findTierGroupAverages(String bojId);

    Map<TierGroup, List<TierAverage>> findTierAverages(String bojId);

    List<SolvedWithTags> findSolvedWithTags(String bojId);
}
