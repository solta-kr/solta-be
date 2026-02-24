package kr.solta.adapter.webapi;

import kr.solta.application.provided.FeedReader;
import kr.solta.application.provided.response.FeedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedReader feedReader;

    @GetMapping("/recent")
    public FeedResponse getRecentFeed() {
        return feedReader.getRecentFeed();
    }
}
