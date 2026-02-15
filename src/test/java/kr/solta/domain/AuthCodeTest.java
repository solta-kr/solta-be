package kr.solta.domain;

import static kr.solta.support.TestFixtures.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class AuthCodeTest {

    @Test
    void 인증_코드를_생성할_수_있다() {
        //given
        Member member = createMember();
        String code = "Ab3xYz";

        //when
        AuthCode authCode = new AuthCode(member, code);

        //then
        assertThat(authCode.getCode()).isEqualTo(code);
        assertThat(authCode.getMember()).isEqualTo(member);
    }

    @Test
    void 인증_코드가_6자리가_아니면_예외가_발생한다() {
        //given
        Member member = createMember();
        String invalidCode = "Ab3xYzQw";

        //when //then
        assertThatThrownBy(() -> new AuthCode(member, invalidCode))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 인증_코드가_null이면_예외가_발생한다() {
        //given
        Member member = createMember();

        //when //then
        assertThatThrownBy(() -> new AuthCode(member, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}