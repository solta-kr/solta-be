package kr.solta.adapter.webapi.response;

import kr.solta.domain.Problem;
import kr.solta.domain.SolveType;
import kr.solta.domain.Solved;
import kr.solta.domain.Tier;

public record SolvedWithAverageResponse(
        Long solvedId,
        SolveType solveType,
        int solveTimeSeconds,
        ProblemDetail problem,
        double averageTime
) {

    record ProblemDetail(
            long problemId,
            long bojProblemId,
            String title,
            Tier tier
    ) {
    }

    public static SolvedWithAverageResponse of(Solved solved, Double averageTime) {
        Problem problem = solved.getProblem();

        return new SolvedWithAverageResponse(
                solved.getId(),
                solved.getSolveType(),
                solved.getSolveTimeSeconds(),
                new ProblemDetail(
                        problem.getId(),
                        problem.getBojProblemId(),
                        problem.getTitle(),
                        problem.getTier()
                ),
                averageTime
        );
    }
}
