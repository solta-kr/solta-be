package kr.solta.application.provided.response;

import java.util.List;
import kr.solta.domain.Solved;
import kr.solta.domain.Tag;

public record SolvedWithTags(
        Solved solved,
        List<Tag> tags
) {
}
