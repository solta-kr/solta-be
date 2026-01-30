package kr.solta.application.provided;

import kr.solta.application.provided.response.MemberProfileResponse;
import kr.solta.application.provided.response.SolveTimeTrendsResponse;
import kr.solta.domain.Member;
import kr.solta.domain.SolvedPeriod;
import kr.solta.domain.TierGroup;

public interface MemberReader {

    Member getMemberById(final Long memberId);

    MemberProfileResponse getMemberProfile(final String name);

    SolveTimeTrendsResponse getSolveTimeTrends(final String name, final SolvedPeriod solvedPeriod,
                                               final TierGroup tierGroup);
}
