package kr.solta.support;

import java.util.Collections;
import java.util.List;
import kr.solta.application.required.SolvedAcClient;
import kr.solta.application.required.dto.SolvedAcProblemResponse;

public class FakeSolvedAcClient implements SolvedAcClient {

    @Override
    public List<SolvedAcProblemResponse> lookupProblems(List<Integer> problemIds) {
        return Collections.emptyList();
    }
}