package kr.solta.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.ProblemTagRepository;
import kr.solta.application.required.TagRepository;
import kr.solta.application.required.dto.SolvedAcDisplayName;
import kr.solta.application.required.dto.SolvedAcProblemResponse;
import kr.solta.application.required.dto.SolvedAcTagResponse;
import kr.solta.domain.Problem;
import kr.solta.domain.ProblemTag;
import kr.solta.domain.Tag;
import kr.solta.domain.Tier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProblemSyncTransactionHandler {

    private final ProblemRepository problemRepository;
    private final ProblemTagRepository problemTagRepository;
    private final TagRepository tagRepository;

    @Transactional
    public int updateExistingBatch(final List<Problem> problems, final List<SolvedAcProblemResponse> responses) {
        Map<Long, SolvedAcProblemResponse> responseMap = responses.stream()
                .collect(Collectors.toMap(SolvedAcProblemResponse::problemId, Function.identity()));

        int updatedCount = 0;

        for (Problem problem : problems) {
            SolvedAcProblemResponse response = responseMap.get(problem.getBojProblemId());
            if (response == null) {
                continue;
            }

            Tier newTier = Tier.getTier(response.level());
            if (problem.getTier() != newTier) {
                problem.updateTier(newTier);
                updatedCount++;
            }

            syncTags(problem, response.tags());
        }

        return updatedCount;
    }

    @Transactional
    public int insertNewBatch(final List<SolvedAcProblemResponse> responses) {
        List<Problem> newProblems = new ArrayList<>();

        for (SolvedAcProblemResponse response : responses) {
            Tier tier = Tier.getTier(response.level());
            Problem problem = new Problem(response.titleKo(), response.problemId(), tier);
            newProblems.add(problem);
        }

        List<Problem> savedProblems = problemRepository.saveAll(newProblems);

        for (int i = 0; i < savedProblems.size(); i++) {
            syncTags(savedProblems.get(i), responses.get(i).tags());
        }

        return savedProblems.size();
    }

    private void syncTags(final Problem problem, final List<SolvedAcTagResponse> tagResponses) {
        if (tagResponses == null || tagResponses.isEmpty()) {
            return;
        }

        List<ProblemTag> existingProblemTags = problemTagRepository.findAllByProblemIn(List.of(problem));
        Set<Integer> existingTagIds = existingProblemTags.stream()
                .map(pt -> pt.getTag().getId())
                .collect(Collectors.toSet());

        List<ProblemTag> newProblemTags = new ArrayList<>();

        for (SolvedAcTagResponse tagResponse : tagResponses) {
            Tag tag = upsertTag(tagResponse);
            if (!existingTagIds.contains(tag.getId())) {
                newProblemTags.add(new ProblemTag(problem, tag));
            }
        }

        if (!newProblemTags.isEmpty()) {
            problemTagRepository.saveAll(newProblemTags);
        }
    }

    private Tag upsertTag(final SolvedAcTagResponse tagResponse) {
        return tagRepository.findByKey(tagResponse.key())
                .orElseGet(() -> {
                    String korName = tagResponse.displayNames().stream()
                            .filter(d -> "ko".equals(d.language()))
                            .map(SolvedAcDisplayName::name)
                            .findFirst()
                            .orElse(tagResponse.key());

                    return tagRepository.save(new Tag(tagResponse.bojTagId(), tagResponse.key(), korName));
                });
    }
}
