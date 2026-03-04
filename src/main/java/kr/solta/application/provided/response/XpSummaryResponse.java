package kr.solta.application.provided.response;

import kr.solta.domain.LevelRange;
import kr.solta.domain.Member;

public record XpSummaryResponse(
        int totalXp,
        int level,
        String title,
        int currentLevelXp,
        int nextLevelRequiredXp,
        int progressPercent
) {
    public static XpSummaryResponse from(final Member member) {
        int totalXp = member.getTotalXp();
        int level = member.getLevel();
        return new XpSummaryResponse(
                totalXp,
                level,
                LevelRange.getTitle(level),
                LevelRange.getCurrentLevelXp(totalXp, level),
                LevelRange.getXpForNextLevel(level),
                LevelRange.getProgressPercent(totalXp, level)
        );
    }
}
