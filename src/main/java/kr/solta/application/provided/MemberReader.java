package kr.solta.application.provided;

import kr.solta.application.provided.response.MemberPage;
import kr.solta.application.provided.response.MemberProfileResponse;
import kr.solta.domain.Member;

public interface MemberReader {

    Member getMemberById(final Long memberId);

    MemberProfileResponse getMemberProfile(final String name);

    MemberPage searchMembers(final String query, final Long lastMemberId);
}
