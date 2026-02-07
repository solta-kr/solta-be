package kr.solta.application.provided.response;

import java.util.List;
import kr.solta.application.required.dto.ProblemSolvedStats;
import kr.solta.domain.Problem;
import kr.solta.domain.Tag;

public record ProblemDetail(
        Problem problem,
        List<Tag> tags,
        ProblemSolvedStats solvedStats
) {
}