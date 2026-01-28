package kr.solta.application.required;

import kr.solta.application.required.dto.GithubTokenResponse;
import kr.solta.application.required.dto.GithubUserResponse;

public interface GithubClient {

    GithubTokenResponse getAccessToken(String code);

    GithubUserResponse getUserInfo(String accessToken);
}