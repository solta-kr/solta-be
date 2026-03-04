package kr.solta.adapter.webapi.response;

import java.time.LocalDateTime;
import java.util.List;
import kr.solta.application.provided.response.SolvedWithTags;
import kr.solta.domain.SolveType;
import kr.solta.domain.Solved;
import kr.solta.domain.Tag;
import kr.solta.domain.Tier;

public record RecentSolvedResponse(
        Long solvedId,
        SolveType solveType,
        Integer solveTimeSeconds,
        String memo,
        Integer earnedXp,
        ProblemDetail problem,
        LocalDateTime createdAt
) {
    record ProblemDetail(
            long problemId,
            long bojProblemId,
            String title,
            Tier tier,
            List<String> tags
    ) {
    }

    public static RecentSolvedResponse from(final SolvedWithTags solvedWithTags, final Long viewerMemberId) {
        Solved solved = solvedWithTags.solved();
        boolean isOwner = viewerMemberId != null && viewerMemberId.equals(solved.getMember().getId());

        return new RecentSolvedResponse(
                solved.getId(),
                solved.getSolveType(),
                solved.getSolveTimeSeconds(),
                isOwner ? solved.getMemo() : null,
                solvedWithTags.earnedXp(),
                new ProblemDetail(
                        solved.getProblem().getId(),
                        solved.getProblem().getBojProblemId(),
                        solved.getProblem().getTitle(),
                        solved.getProblem().getTier(),
                        solvedWithTags.tags().stream()
                                .map(Tag::getKorName)
                                .toList()
                ),
                solved.getSolvedTime()
        );
    }
}
