package kr.solta.adapter.webapi.response;

import kr.solta.domain.Member;

public record MemberResponse(
        Long id,
        String name,
        Long githubId,
        String bojId,
        String avatarUrl
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getGithubId(),
                member.getBojId(),
                member.getAvatarUrl()
        );
    }
}
