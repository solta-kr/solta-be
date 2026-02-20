package kr.solta.application.provided;

import kr.solta.application.provided.request.AuthMember;

public interface SolvedMemoUpdater {

    void update(final AuthMember authMember, final Long solvedId, final String memo);
}
