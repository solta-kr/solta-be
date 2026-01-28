package kr.solta.application;

import kr.solta.application.required.GithubClient;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.TokenProvider;
import kr.solta.application.required.dto.GithubTokenResponse;
import kr.solta.application.required.dto.GithubUserResponse;
import kr.solta.application.required.dto.TokenPayload;
import kr.solta.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final GithubClient githubClient;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    public String createAuthToken(final String code) {
        GithubTokenResponse githubTokenResponse = githubClient.getAccessToken(code);
        GithubUserResponse githubClientUserInfo = githubClient.getUserInfo(githubTokenResponse.accessToken());

        Member member = findOrCreteMember(githubClientUserInfo);

        return tokenProvider.issue(new TokenPayload(member.getId(), member.getName()));
    }

    private Member findOrCreteMember(final GithubUserResponse githubUserResponse) {
        return memberRepository.findByGithubId(githubUserResponse.id())
                .orElseGet(
                        () -> memberRepository.save(
                                Member.create(
                                        githubUserResponse.id(),
                                        githubUserResponse.login(),
                                        githubUserResponse.avatarUrl()
                                )
                        )
                );
    }
}
