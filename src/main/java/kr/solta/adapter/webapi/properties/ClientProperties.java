package kr.solta.adapter.webapi.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "client")
public record ClientProperties(
        String extensionId,
        String webUrl
) {
}
