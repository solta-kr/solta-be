package kr.solta.adapter.webapi.response;

import kr.solta.domain.Problem;
import kr.solta.domain.Solved;
import kr.solta.domain.Tier;

public record SolvedRegisterResponse(
        long solvedId,
        Integer solveTimeSeconds,
        String memo,
        ProblemDetail problem
) {
    record ProblemDetail(
            long bojProblemId,
            String title,
            Tier tier
    ) {
    }

    public static SolvedRegisterResponse from(final Solved solved) {
        Problem problem = solved.getProblem();

        return new SolvedRegisterResponse(
                solved.getId(),
                solved.getSolveTimeSeconds(),
                solved.getMemo(),
                new ProblemDetail(
                        problem.getBojProblemId(),
                        problem.getTitle(),
                        problem.getTier()
                )
        );
    }
}
