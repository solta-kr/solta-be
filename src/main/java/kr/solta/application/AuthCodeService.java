package kr.solta.application;

import kr.solta.application.exception.NotFoundEntityException;
import kr.solta.application.provided.AuthCodeCreator;
import kr.solta.application.provided.request.AuthMember;
import kr.solta.application.required.AuthCodeRepository;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.RandomCodeGenerator;
import kr.solta.domain.AuthCode;
import kr.solta.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthCodeService implements AuthCodeCreator {

    private final MemberRepository memberRepository;
    private final AuthCodeRepository authCodeRepository;
    private final RandomCodeGenerator randomCodeGenerator;

    @Transactional
    @Override
    public AuthCode create(final AuthMember authMember) {
        Member member = getMemberById(authMember.memberId());

        String code = randomCodeGenerator.generate(AuthCode.CODE_LENGTH);
        AuthCode authCode = new AuthCode(member, code);

        return authCodeRepository.save(authCode);
    }

    private Member getMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundEntityException("회원을 찾을 수 없습니다. memberId: " + memberId));
    }
}
