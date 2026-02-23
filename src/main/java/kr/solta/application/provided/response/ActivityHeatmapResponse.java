package kr.solta.application.provided.response;

import java.util.List;

public record ActivityHeatmapResponse(
        List<ActivityData> activities,
        int currentStreak,
        int longestStreak
) {
}
