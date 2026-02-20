package kr.solta.application.provided.request;

import jakarta.validation.constraints.NotNull;
import kr.solta.domain.SolveType;

public record SolvedRegisterRequest(
        @NotNull SolveType solveType,
        @NotNull Long bojProblemId,
        Integer solveTimeSeconds,
        String memo
) {

}
