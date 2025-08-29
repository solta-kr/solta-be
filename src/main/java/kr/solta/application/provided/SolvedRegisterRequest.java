package kr.solta.application.provided;

import jakarta.validation.constraints.NotNull;
import kr.solta.domain.SolveType;

public record SolvedRegisterRequest(
        @NotNull String bojId,
        @NotNull SolveType solveType,
        @NotNull Long bojProblemId,
        @NotNull int solveTimeSeconds
) {

}
