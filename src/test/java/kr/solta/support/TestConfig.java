package kr.solta.support;

import kr.solta.application.required.GithubClient;
import kr.solta.application.required.SolvedAcClient;
import kr.solta.application.required.SolvedAcRateLimiter;
import kr.solta.application.required.TokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public GithubClient githubClient() {
        return new FakeGithubClient();
    }

    @Bean
    @Primary
    public TokenProvider tokenProvider() {
        return new FakeTokenProvider();
    }

    @Bean
    @Primary
    public SolvedAcClient solvedAcClient() {
        return new FakeSolvedAcClient();
    }

    @Bean
    @Primary
    public SolvedAcRateLimiter solvedAcRateLimiter() {
        return new FakeSolvedAcRateLimiter();
    }
}
