package kr.solta.application.provided.response;

public record FeedStatsResponse(
        String periodLabel,
        long activeUserCount,
        long totalSolveCount
) {}
