package kr.solta.application;

import kr.solta.application.exception.NotFoundEntityException;
import kr.solta.application.provided.SolvedRegister;
import kr.solta.application.provided.SolvedRegisterRequest;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.Solved;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class SolvedService implements SolvedRegister {

    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;
    private final SolvedRepository solvedRepository;

    @Override
    public Solved register(SolvedRegisterRequest solvedRegisterRequest) {
        Member solvedMember = findOrCreteMember(solvedRegisterRequest.bojId().trim());
        Problem problem = getProblem(solvedRegisterRequest);

        Solved solved = Solved.submit(solvedRegisterRequest.solveTimeSeconds(), solvedMember, problem);

        return solvedRepository.save(solved);
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
}
