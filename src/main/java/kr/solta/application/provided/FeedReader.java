package kr.solta.application.provided;

import kr.solta.application.provided.response.FeedResponse;

public interface FeedReader {
    FeedResponse getRecentFeed();
}
