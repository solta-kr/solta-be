package kr.solta.adapter.github;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.github")
public record GithubProperties(
        String loginUrl,
        String clientId,
        String clientSecret,
        String redirectUrl,
        String clientSuccessUrl
) {
}
