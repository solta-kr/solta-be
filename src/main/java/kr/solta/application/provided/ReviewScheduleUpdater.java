package kr.solta.application.provided;

import kr.solta.application.provided.request.AuthMember;

public interface ReviewScheduleUpdater {

    void skip(final AuthMember authMember, final Long reviewScheduleId);

    void reschedule(final AuthMember authMember, final Long reviewScheduleId, final int intervalDays);

    void updateDefaultReviewInterval(final AuthMember authMember, final int intervalDays);
}
