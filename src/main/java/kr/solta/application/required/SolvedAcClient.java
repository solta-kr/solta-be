package kr.solta.application.required;

import java.util.List;
import kr.solta.application.required.dto.SolvedAcProblemResponse;

public interface SolvedAcClient {

    List<SolvedAcProblemResponse> lookupProblems(List<Long> problemIds);
}
