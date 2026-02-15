package kr.solta.application.provided;

import static kr.solta.support.FakeRandomCodeGenerator.FAKE_CODE;
import static kr.solta.support.TestFixtures.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import kr.solta.application.provided.request.AuthMember;
import kr.solta.application.required.AuthCodeRepository;
import kr.solta.application.required.MemberRepository;
import kr.solta.domain.AuthCode;
import kr.solta.domain.Member;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuthCodeCreatorTest extends IntegrationTest {

    @Autowired
    private AuthCodeCreator authCodeCreator;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthCodeRepository authCodeRepository;

    @Test
    void 회원의_인증_코드를_생성할_수_있다() {
        //given
        Member member = memberRepository.save(createMember());
        AuthMember authMember = new AuthMember(member.getId());

        //when
        AuthCode authCode = authCodeCreator.create(authMember);

        //then
        assertSoftly(softly -> {
            softly.assertThat(authCode.getId()).isNotNull();
            softly.assertThat(authCode.getCode()).isEqualTo(FAKE_CODE);
            softly.assertThat(authCode.getMember().getId()).isEqualTo(member.getId());
        });
    }

    @Test
    void 이미_인증_코드가_존재하면_기존_코드를_반환한다() {
        //given
        Member member = memberRepository.save(createMember());
        AuthMember authMember = new AuthMember(member.getId());
        AuthCode existing = authCodeRepository.save(new AuthCode(member, FAKE_CODE));

        //when
        AuthCode authCode = authCodeCreator.create(authMember);

        //then
        assertThat(authCode.getId()).isEqualTo(existing.getId());
    }

    @Test
    void 존재하지_않는_회원이면_예외가_발생한다() {
        //given
        AuthMember authMember = new AuthMember(999L);

        //when //then
        assertThatThrownBy(() -> authCodeCreator.create(authMember))
                .isInstanceOf(RuntimeException.class);
    }
}