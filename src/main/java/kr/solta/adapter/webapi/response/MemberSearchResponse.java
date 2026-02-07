package kr.solta.adapter.webapi.response;

import java.util.List;
import kr.solta.application.provided.response.MemberPage;
import kr.solta.domain.Member;

public record MemberSearchResponse(
        List<MemberItem> members,
        Long nextLastMemberId,
        boolean hasNext
) {
    public record MemberItem(
            long memberId,
            String name,
            String avatarUrl
    ) {
        public static MemberItem from(Member member) {
            return new MemberItem(
                    member.getId(),
                    member.getName(),
                    member.getAvatarUrl()
            );
        }
    }

    public static MemberSearchResponse from(MemberPage page) {
        List<MemberItem> items = page.members().stream()
                .map(MemberItem::from)
                .toList();

        Long nextLastMemberId = page.hasNext() && !page.members().isEmpty()
                ? page.members().getLast().getId()
                : null;

        return new MemberSearchResponse(items, nextLastMemberId, page.hasNext());
    }
}