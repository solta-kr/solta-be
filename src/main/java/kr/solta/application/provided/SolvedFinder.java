package kr.solta.application.provided;

import java.util.List;
import java.util.Map;
import kr.solta.application.provided.response.SolvedWithTags;
import kr.solta.domain.Problem;
import kr.solta.domain.Solved;
import kr.solta.domain.SolvedAverage;
import kr.solta.domain.TierAverage;
import kr.solta.domain.TierGroup;
import kr.solta.domain.TierGroupAverage;

public interface SolvedFinder {

    List<Solved> findSolveds(String bojId);

    List<SolvedAverage> findSolvedAverages(List<Problem> problems);

    List<TierGroupAverage> findTierGroupAverages(String bojId);

    Map<TierGroup, List<TierAverage>> findTierAverages(String bojId);

    List<SolvedWithTags> findSolvedWithTags(String bojId);
}
