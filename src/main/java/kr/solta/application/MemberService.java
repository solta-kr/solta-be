package kr.solta.application;

import java.util.List;
import kr.solta.application.provided.MemberReader;
import kr.solta.application.provided.response.MemberPage;
import kr.solta.application.provided.response.MemberProfileResponse;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.application.required.dto.AllSolvedAverage;
import kr.solta.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService implements MemberReader {

    private static final int PAGE_SIZE = 10;

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
        Member member = memberRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. name: " + name));
        AllSolvedAverage allSolvedAverage = solvedRepository.findAllSolvedAverage(member);

        return MemberProfileResponse.of(member, allSolvedAverage);
    }

    @Transactional(readOnly = true)
    @Override
    public MemberPage searchMembers(final String query, final Long lastMemberId) {
        long cursorValue = lastMemberId != null ? lastMemberId : 0L;
        int fetchSize = PAGE_SIZE + 1;
        PageRequest pageRequest = PageRequest.of(0, fetchSize);

        List<Member> members;
        if (query == null || query.isBlank()) {
            members = memberRepository.findAllAfterMemberId(cursorValue, pageRequest);
        } else {
            members = memberRepository.searchByNamePrefixAfterMemberId(query, cursorValue, pageRequest);
        }

        boolean hasNext = members.size() > PAGE_SIZE;
        if (hasNext) {
            members = members.subList(0, PAGE_SIZE);
        }

        return new MemberPage(members, hasNext);
    }
}
