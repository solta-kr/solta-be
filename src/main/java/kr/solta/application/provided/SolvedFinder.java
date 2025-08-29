package kr.solta.application.provided;

import java.util.List;
import kr.solta.domain.Problem;
import kr.solta.domain.Solved;
import kr.solta.domain.SolvedAverage;

public interface SolvedFinder {

    List<Solved> findSolveds(String bojId);

    List<SolvedAverage> findSolvedAverages(List<Problem> problems);
}
