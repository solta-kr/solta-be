package kr.solta.adapter.github;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import kr.solta.application.required.GithubClient;
import kr.solta.application.required.dto.GithubTokenResponse;
import kr.solta.application.required.dto.GithubUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class GithubRestClient implements GithubClient {

    private static final String API_VERSION_HEADER = "X-GitHub-Api-Version";
    private static final String API_VERSION = "2022-11-28";
    private static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_USER_URL = "https://api.github.com/user";

    private final RestClient restClient;
    private final GithubProperties githubProperties;

    public GithubRestClient(final RestClient.Builder restClientBuilder, final GithubProperties githubProperties) {
        this.githubProperties = githubProperties;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        this.restClient = restClientBuilder
                .requestFactory(requestFactory)
                .defaultHeader(API_VERSION_HEADER, API_VERSION)
                .defaultStatusHandler(
                        HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            InputStream bodyStream = response.getBody();
                            String body = new String(bodyStream.readAllBytes(), StandardCharsets.UTF_8);
                            throw new IllegalArgumentException("[GitHub] 클라이언트 오류: " + body);
                        }
                )
                .defaultStatusHandler(
                        HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            InputStream bodyStream = response.getBody();
                            String body = new String(bodyStream.readAllBytes(), StandardCharsets.UTF_8);
                            throw new RuntimeException("[GitHub] 예기치 못한 예외 발생: " + body);
                        }
                )
                .build();
    }

    @Override
    public GithubTokenResponse getAccessToken(final String code) {
        return restClient.post()
                .uri(GITHUB_TOKEN_URL +
                        "?client_id=" + githubProperties.clientId() +
                        "&client_secret=" + githubProperties.clientSecret() +
                        "&code=" + code)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .body(GithubTokenResponse.class);
    }

    @Override
    public GithubUserResponse getUserInfo(final String accessToken) {
        return restClient.get()
                .uri(GITHUB_USER_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(GithubUserResponse.class);
    }
}
