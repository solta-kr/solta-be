package kr.solta.application.provided.response;

import java.util.List;

public record BadgeStatsResponse(
        String username,
        int totalMinutes,
        int avgMinutes,
        int selfSolveRate,
        List<TierDataItem> tierData
) {

    public record TierDataItem(
            String label,
            int avgMinutes,
            String color
    ) {}
}
