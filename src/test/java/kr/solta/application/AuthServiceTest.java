package kr.solta.application;

import static kr.solta.support.FakeGithubClient.FAKE_AVATAR_URL;
import static kr.solta.support.FakeGithubClient.FAKE_GITHUB_ID;
import static kr.solta.support.FakeGithubClient.FAKE_LOGIN;
import static kr.solta.support.TestFixtures.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.TokenProvider;
import kr.solta.application.required.dto.TokenPayload;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuthServiceTest extends IntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    void Github_코드로_서버에서_사용할_인증_토큰을_생성할_수_있다() {
        //given
        String code = "github-auth-code";

        //when
        String token = authService.createAuthToken(code);

        //then
        assertThat(token).isNotNull();
    }

    @Test
    void 처음_로그인하는_사용자는_자동으로_회원가입된다() {
        //given
        String code = "github-auth-code";
        long initialMemberCount = memberRepository.count();

        //when
        String authToken = authService.createAuthToken(code);

        //then
        assertSoftly(softly -> {
            softly.assertThat(memberRepository.count()).isEqualTo(initialMemberCount + 1);

            TokenPayload tokenPayload = tokenProvider.extractPayload(authToken);
            softly.assertThat(memberRepository.findById(tokenPayload.memberId()).isPresent());
        });
    }

    @Test
    void 기존_회원이_로그인하면_새로운_회원을_생성하지_않는다() {
        //given
        memberRepository.save(createMember(FAKE_GITHUB_ID, FAKE_LOGIN, FAKE_AVATAR_URL));
        String code = "github-auth-code";
        long initialMemberCount = memberRepository.count();

        //when
        authService.createAuthToken(code);

        //then
        assertThat(memberRepository.count()).isEqualTo(initialMemberCount);
    }
}
