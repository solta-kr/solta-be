package kr.solta.application;

import java.util.ArrayList;
import java.util.List;
import kr.solta.application.exception.NotFoundEntityException;
import kr.solta.application.provided.SolvedFinder;
import kr.solta.application.provided.SolvedRegister;
import kr.solta.application.provided.request.SolvedRegisterRequest;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.application.required.dto.TierGroupSolveStats;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.Solved;
import kr.solta.domain.SolvedAverage;
import kr.solta.domain.Tier;
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

    @Override
    public Solved register(SolvedRegisterRequest solvedRegisterRequest) {
        Member solvedMember = findOrCreteMember(solvedRegisterRequest.bojId().trim());
        Problem problem = getProblem(solvedRegisterRequest);

        Solved solved = Solved.register(
                solvedRegisterRequest.solveTimeSeconds(),
                solvedRegisterRequest.solveType(),
                solvedMember,
                problem
        );

        return solvedRepository.save(solved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Solved> findSolveds(String bojId) {
        Member member = getMember(bojId);

        return solvedRepository.findByMemberOrderByCreatedAtDesc(member);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SolvedAverage> findSolvedAverages(List<Problem> problems) {
        return solvedRepository.findSolvedAveragesByProblems(problems);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TierGroupAverage> findTierGroupAverages(final String bojId) {
        Member member = getMember(bojId);

        List<TierGroupAverage> tierGroupAverages = new ArrayList<>();
        for (TierGroup tierGroup : TierGroup.values()) {
            TierGroupSolveStats tierGroupSolveStats = solvedRepository.calculateTierGroupAverageByMember(
                    member,
                    Tier.getByGroup(tierGroup)
            );
            tierGroupAverages.add(
                    new TierGroupAverage(tierGroup, tierGroupSolveStats.average(), tierGroupSolveStats.count())
            );
        }
        
        return tierGroupAverages;
    }

    private Problem getProblem(SolvedRegisterRequest solvedRegisterRequest) {
        return problemRepository.findByBojProblemId(solvedRegisterRequest.bojProblemId())
                .orElseThrow(() -> new NotFoundEntityException(
                        "백준 문제 번호: " + solvedRegisterRequest.bojProblemId() + " 에 해당하는 문제가 존재하지 않습니다.")
                );
    }

    private Member findOrCreteMember(String bojId) {
        return memberRepository.findByBojId(bojId)
                .orElseGet(() -> memberRepository.save(Member.create(bojId)));
    }

    private Member getMember(String bojId) {
        return memberRepository.findByBojId(bojId)
                .orElseThrow(() -> new NotFoundEntityException("등록되지 않은 백준 ID입니다."));
    }
}
