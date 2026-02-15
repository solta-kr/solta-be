package kr.solta.application.provided;

import static kr.solta.support.FakeBojClient.FAKE_BOJ_ID;
import static kr.solta.support.FakeRandomCodeGenerator.FAKE_CODE;
import static kr.solta.support.TestFixtures.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import kr.solta.application.exception.NotFoundEntityException;
import kr.solta.application.provided.request.AuthMember;
import kr.solta.application.required.AuthCodeRepository;
import kr.solta.application.required.MemberRepository;
import kr.solta.domain.AuthCode;
import kr.solta.domain.Member;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BojIdVerifierTest extends IntegrationTest {

    @Autowired
    private BojIdVerifier bojIdVerifier;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthCodeRepository authCodeRepository;

    @Test
    void 인증코드가_일치하면_bojId가_업데이트된다() {
        //given
        Member member = memberRepository.save(createMember());
        authCodeRepository.save(new AuthCode(member, FAKE_CODE));
        AuthMember authMember = new AuthMember(member.getId());

        //when
        bojIdVerifier.verify(authMember, "http://boj.kr/abc123");

        //then
        Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updatedMember.getBojId()).isEqualTo(FAKE_BOJ_ID);
    }

    @Test
    void 인증코드가_일치하면_인증코드가_삭제된다() {
        //given
        Member member = memberRepository.save(createMember());
        authCodeRepository.save(new AuthCode(member, FAKE_CODE));
        AuthMember authMember = new AuthMember(member.getId());

        //when
        bojIdVerifier.verify(authMember, "http://boj.kr/abc123");

        //then
        assertThat(authCodeRepository.findByMember(member)).isEmpty();
    }

    @Test
    void 인증코드가_일치하지_않으면_예외가_발생한다() {
        //given
        Member member = memberRepository.save(createMember());
        authCodeRepository.save(new AuthCode(member, "Zz9aXb"));
        AuthMember authMember = new AuthMember(member.getId());

        //when //then
        assertThatThrownBy(() -> bojIdVerifier.verify(authMember, "http://boj.kr/abc123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("인증 코드가 일치하지 않습니다.");
    }

    @Test
    void 인증코드가_존재하지_않으면_예외가_발생한다() {
        //given
        Member member = memberRepository.save(createMember());
        AuthMember authMember = new AuthMember(member.getId());

        //when //then
        assertThatThrownBy(() -> bojIdVerifier.verify(authMember, "http://boj.kr/abc123"))
                .isInstanceOf(NotFoundEntityException.class);
    }
}
