package kr.solta.application;

import kr.solta.application.provided.MemberReader;
import kr.solta.application.provided.response.MemberProfileResponse;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.application.required.dto.AllSolvedAverage;
import kr.solta.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService implements MemberReader {

    private final MemberRepository memberRepository;
    private final SolvedRepository solvedRepository;

    @Transactional(readOnly = true)
    @Override
    public Member getMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. memberId: " + memberId));
    }

    @Transactional(readOnly = true)
    @Override
    public MemberProfileResponse getMemberProfile(final String name) {
        Member member = getMemberByName(name);
        AllSolvedAverage allSolvedAverage = solvedRepository.findAllSolvedAverage(member);

        return MemberProfileResponse.of(member, allSolvedAverage);
    }

    private Member getMemberByName(final String name) {
        return memberRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. name: " + name));
    }
}
