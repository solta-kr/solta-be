package kr.solta.support;

import kr.solta.application.required.GithubClient;
import kr.solta.application.required.dto.GithubTokenResponse;
import kr.solta.application.required.dto.GithubUserResponse;

public class FakeGithubClient implements GithubClient {

    public static final Long FAKE_GITHUB_ID = 12345L;
    public static final String FAKE_LOGIN = "testuser";
    public static final String FAKE_NAME = "Test User";
    public static final String FAKE_EMAIL = "test@example.com";
    public static final String FAKE_AVATAR_URL = "https://avatar.example.com/test.png";
    public static final String FAKE_ACCESS_TOKEN = "fake-access-token";

    @Override
    public GithubTokenResponse getAccessToken(String code) {
        return new GithubTokenResponse(FAKE_ACCESS_TOKEN, "bearer", "read:user");
    }

    @Override
    public GithubUserResponse getUserInfo(String accessToken) {
        return new GithubUserResponse(
                FAKE_GITHUB_ID,
                FAKE_LOGIN,
                FAKE_NAME,
                FAKE_EMAIL,
                FAKE_AVATAR_URL
        );
    }
}