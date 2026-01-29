package kr.solta.support;

import kr.solta.application.required.TokenProvider;
import kr.solta.application.required.dto.TokenPayload;

public class FakeTokenProvider implements TokenProvider {

    @Override
    public String issue(TokenPayload tokenPayload) {
        return "fake-token-" + tokenPayload.memberId();
    }

    @Override
    public TokenPayload extractPayload(String token) {
        String memberId = token.replace("fake-token-", "");
        return new TokenPayload(Long.parseLong(memberId), "testuser");
    }
}