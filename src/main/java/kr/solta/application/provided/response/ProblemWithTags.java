package kr.solta.application.provided.response;

import java.util.List;
import kr.solta.domain.Problem;
import kr.solta.domain.Tag;

public record ProblemWithTags(
        Problem problem,
        List<Tag> tags
) {
}