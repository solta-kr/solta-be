package kr.solta.application.provided.response;

import kr.solta.application.required.dto.AllSolvedAverage;
import kr.solta.domain.Member;

public record MemberProfileResponse(
        Long memberId,
        String name,
        String bojId,
        String avatarUrl,
        long solvedCount,
        double totalSolvedTime,
        double totalSolvedAverageTime
) {

    public static MemberProfileResponse of(final Member member, final AllSolvedAverage allSolvedAverage) {
        return new MemberProfileResponse(
                member.getId(),
                member.getName(),
                member.getBojId(),
                member.getAvatarUrl(),
                allSolvedAverage.solvedCount() == null ? 0 : allSolvedAverage.solvedCount(),
                allSolvedAverage.totalSolvedTime() == null ? 0 : allSolvedAverage.totalSolvedTime(),
                allSolvedAverage.totalSolvedAverageTime() == null ? 0 : allSolvedAverage.totalSolvedAverageTime()
        );
    }
}
