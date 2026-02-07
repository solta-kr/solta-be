package kr.solta.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.solta.application.provided.ProblemFinder;
import kr.solta.application.provided.response.ProblemPage;
import kr.solta.application.provided.response.ProblemWithTags;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.ProblemTagRepository;
import kr.solta.domain.Problem;
import kr.solta.domain.ProblemTag;
import kr.solta.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProblemService implements ProblemFinder {

    private static final int PAGE_SIZE = 10;

    private final ProblemRepository problemRepository;
    private final ProblemTagRepository problemTagRepository;

    @Override
    public ProblemPage searchProblems(final String query, final Long lastBojProblemId) {
        long cursorValue = lastBojProblemId != null ? lastBojProblemId : 0L;
        int fetchSize = PAGE_SIZE + 1;

        PageRequest pageRequest = PageRequest.of(0, fetchSize);

        List<Problem> problems;
        if (query == null || query.isBlank()) {
            problems = problemRepository.findAllAfterBojProblemId(cursorValue, pageRequest);
        } else {
            problems = problemRepository.searchByBojProblemIdPrefixAfter(query, cursorValue, pageRequest);
        }

        boolean hasNext = problems.size() > PAGE_SIZE;
        if (hasNext) {
            problems = problems.subList(0, PAGE_SIZE);
        }

        Map<Problem, List<Tag>> tagsByProblem = getTagsByProblem(problems);

        List<ProblemWithTags> problemWithTags = problems.stream()
                .map(problem -> new ProblemWithTags(
                        problem,
                        tagsByProblem.getOrDefault(problem, List.of())
                ))
                .toList();

        return new ProblemPage(problemWithTags, hasNext);
    }

    private Map<Problem, List<Tag>> getTagsByProblem(final List<Problem> problems) {
        if (problems.isEmpty()) {
            return Map.of();
        }

        List<ProblemTag> problemTags = problemTagRepository.findByProblemsWithTag(problems);

        return problemTags.stream()
                .collect(Collectors.groupingBy(
                        ProblemTag::getProblem,
                        Collectors.mapping(ProblemTag::getTag, Collectors.toList())
                ));
    }
}