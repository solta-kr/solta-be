package kr.solta.application.provided;

import jakarta.validation.constraints.NotNull;

public record SolvedRegisterRequest(
        @NotNull String bojId,
        @NotNull Long bojProblemId,
        @NotNull int solveTimeSeconds
) {

}
