package kr.solta.application.provided.response;

import java.util.List;

public record FeedResponse(
        FeedStatsResponse stats,
        List<FeedItemResponse> recentFeeds
) {}
