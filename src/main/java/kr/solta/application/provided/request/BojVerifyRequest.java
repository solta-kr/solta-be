package kr.solta.application.provided.request;

import jakarta.validation.constraints.Pattern;

public record BojVerifyRequest(
        @Pattern(
                regexp = "^(https://www\\.acmicpc\\.net/source/share/[a-f0-9]+|http://boj\\.kr/[a-f0-9]+)$",
                message = "올바른 BOJ 소스 공유 URL이 아닙니다."
        )
        String shareUrl
) {
}
