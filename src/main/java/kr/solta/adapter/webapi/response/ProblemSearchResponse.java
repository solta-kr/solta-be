package kr.solta.adapter.webapi.response;

import java.util.List;
import kr.solta.application.provided.response.ProblemPage;
import kr.solta.application.provided.response.ProblemWithTags;
import kr.solta.domain.Tag;
import kr.solta.domain.Tier;

public record ProblemSearchResponse(
        List<ProblemItem> problems,
        Long nextLastBojProblemId,
        boolean hasNext
) {
    public record ProblemItem(
            long problemId,
            long bojProblemId,
            String title,
            Tier tier,
            List<String> tags
    ) {
        public static ProblemItem from(ProblemWithTags problemWithTags) {
            return new ProblemItem(
                    problemWithTags.problem().getId(),
                    problemWithTags.problem().getBojProblemId(),
                    problemWithTags.problem().getTitle(),
                    problemWithTags.problem().getTier(),
                    problemWithTags.tags().stream()
                            .map(Tag::getKorName)
                            .toList()
            );
        }
    }

    public static ProblemSearchResponse from(ProblemPage page) {
        List<ProblemItem> items = page.problems().stream()
                .map(ProblemItem::from)
                .toList();

        Long nextLastBojProblemId = page.hasNext() && !page.problems().isEmpty()
                ? page.problems().getLast().problem().getBojProblemId()
                : null;

        return new ProblemSearchResponse(items, nextLastBojProblemId, page.hasNext());
    }
}