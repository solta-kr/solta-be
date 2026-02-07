package kr.solta.adapter.webapi.response;

import java.util.List;
import kr.solta.application.provided.response.ProblemDetail;
import kr.solta.domain.Tag;
import kr.solta.domain.Tier;

public record ProblemDetailResponse(
        long problemId,
        long bojProblemId,
        String title,
        Tier tier,
        List<String> tags,
        long totalSolvedCount,
        long independentSolvedCount,
        Double averageSolveTimeSeconds,
        Integer shortestSolveTimeSeconds
) {
    public static ProblemDetailResponse from(ProblemDetail detail) {
        return new ProblemDetailResponse(
                detail.problem().getId(),
                detail.problem().getBojProblemId(),
                detail.problem().getTitle(),
                detail.problem().getTier(),
                detail.tags().stream()
                        .map(Tag::getKorName)
                        .toList(),
                detail.solvedStats().totalSolvedCount(),
                detail.solvedStats().independentSolvedCount(),
                detail.solvedStats().averageSolveTimeSeconds(),
                detail.solvedStats().shortestSolveTimeSeconds()
        );
    }
}