package kr.solta.application.provided;

import kr.solta.application.provided.response.MemberProfileResponse;
import kr.solta.domain.Member;

public interface MemberReader {

    Member getMemberById(final Long memberId);

    MemberProfileResponse getMemberProfile(final String name);
}
