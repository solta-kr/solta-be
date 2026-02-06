package kr.solta.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.solta.application.exception.NotFoundEntityException;
import kr.solta.application.provided.SolvedFinder;
import kr.solta.application.provided.SolvedRegister;
import kr.solta.application.provided.request.AuthMember;
import kr.solta.application.provided.request.SolvedRegisterRequest;
import kr.solta.application.provided.request.SolvedSortType;
import kr.solta.application.provided.request.TagKey;
import kr.solta.application.provided.response.SolvedWithTags;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.ProblemTagRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.application.required.dto.SolvedStats;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.ProblemTag;
import kr.solta.domain.SolveType;
import kr.solta.domain.Solved;
import kr.solta.domain.Tag;
import kr.solta.domain.TierAverage;
import kr.solta.domain.TierAverages;
import kr.solta.domain.TierGroup;
import kr.solta.domain.TierGroupAverage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class SolvedService implements SolvedRegister, SolvedFinder {

    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;
    private final SolvedRepository solvedRepository;
    private final ProblemTagRepository problemTagRepository;

    @Override
    public Solved register(final AuthMember authMember, final SolvedRegisterRequest solvedRegisterRequest) {
        Member solvedMember = getMemberById(authMember.memberId());
        Problem problem = getProblem(solvedRegisterRequest);

        Solved solved = Solved.register(
                solvedRegisterRequest.solveTimeSeconds(),
                solvedRegisterRequest.solveType(),
                solvedMember,
                problem,
                LocalDateTime.now()
        );

        return solvedRepository.save(solved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TierGroupAverage> findTierGroupAverages(final String name, final TagKey tagKey) {
        Member member = getMemberByName(name);

        List<TierGroupAverage> tierGroupAverages = new ArrayList<>();
        for (TierGroup tierGroup : TierGroup.values()) {
            SolvedStats solvedStats = tagKey == null
                    ? solvedRepository.calculateTierGroupAverageByMember(member, tierGroup.getTiers())
                    : solvedRepository.calculateTierGroupAverageByMemberAndTag(member, tierGroup.getTiers(), tagKey.getKey());

            tierGroupAverages.add(
                    new TierGroupAverage(tierGroup, solvedStats.average(), solvedStats.count(), solvedStats.independentCount())
            );
        }

        return tierGroupAverages;
    }

    @Transactional(readOnly = true)
    @Override
    public Map<TierGroup, List<TierAverage>> findTierAverages(final String name) {
        Member member = getMemberByName(name);

        TierAverages tierAverages = new TierAverages(solvedRepository.findTierAverageByMember(member));

        return tierAverages.toTierGroupAverageMap();
    }

    @Transactional(readOnly = true)
    @Override
    public List<SolvedWithTags> findSolvedWithTags(final String name) {
        Member member = getMemberByName(name);

        List<Solved> solveds = solvedRepository.findByMemberOrderByCreatedAtDesc(member);
        Map<Problem, List<Tag>> tagsByProblem = geProblemTags(solveds);

        return solveds.stream()
                .map(solved -> new SolvedWithTags(
                        solved,
                        tagsByProblem.getOrDefault(solved.getProblem(), List.of())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<SolvedWithTags> findProblemsToRetry(final String name, final SolvedSortType sortType) {
        Member member = getMemberByName(name);

        List<Solved> solveds = getRetrySolveds(sortType, member);
        Map<Problem, List<Tag>> tagsByProblem = geProblemTags(solveds);

        return solveds.stream()
                .map(solved -> new SolvedWithTags(
                        solved,
                        tagsByProblem.getOrDefault(solved.getProblem(), List.of())
                ))
                .toList();
    }

    private Problem getProblem(SolvedRegisterRequest solvedRegisterRequest) {
        return problemRepository.findByBojProblemId(solvedRegisterRequest.bojProblemId())
                .orElseThrow(() -> new NotFoundEntityException(
                        "백준 문제 번호: " + solvedRegisterRequest.bojProblemId() + " 에 해당하는 문제가 존재하지 않습니다.")
                );
    }

    private Map<Problem, List<Tag>> geProblemTags(final List<Solved> solveds) {
        List<Problem> problems = solveds.stream()
                .map(Solved::getProblem)
                .toList();

        return getTagsByProblem(problems);
    }

    private Member getMemberById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException("존재하지 않는 사용자입니다."));
    }

    private Member getMemberByName(final String name) {
        return memberRepository.findByName(name)
                .orElseThrow(() -> new NotFoundEntityException("존재하지 않는 사용자입니다."));
    }

    private Map<Problem, List<Tag>> getTagsByProblem(final List<Problem> problems) {
        List<ProblemTag> problemTags = problemTagRepository.findByProblemsWithTag(problems);

        return problemTags.stream()
                .collect(Collectors.groupingBy(
                        ProblemTag::getProblem,
                        Collectors.mapping(ProblemTag::getTag, Collectors.toList())
                ));
    }

    private List<Solved> getRetrySolveds(final SolvedSortType sortType, final Member member) {
        return switch (sortType) {
            case LATEST -> solvedRepository.findByMemberAndSolveTypeOrderBySolvedTimeDesc(
                    member,
                    SolveType.SOLUTION
            );
            case TIER -> solvedRepository.findByMemberAndSolveTypeOrderByLevelDesc(
                    member,
                    SolveType.SOLUTION
            );
            case SOLVE_TIME -> solvedRepository.findByMemberAndSolveTypeOrderBySolveTimeSecondsDesc(
                    member,
                    SolveType.SOLUTION
            );
        };
    }
}
