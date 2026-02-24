package kr.solta.application;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import kr.solta.application.provided.FeedReader;
import kr.solta.application.provided.response.FeedItemResponse;
import kr.solta.application.provided.response.FeedResponse;
import kr.solta.application.provided.response.FeedStatsResponse;
import kr.solta.application.required.SolvedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService implements FeedReader {

    private static final int FEED_SIZE = 10;

    private final SolvedRepository solvedRepository;

    @Override
    public FeedResponse getRecentFeed() {
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);

        long activeUserCount = solvedRepository.countDistinctMembersFromTime(weekStart);
        long totalSolveCount = solvedRepository.countSolvesFromTime(weekStart);

        FeedStatsResponse stats = new FeedStatsResponse("최근 1주일", activeUserCount, totalSolveCount);

        List<FeedItemResponse> recentFeeds = solvedRepository
                .findRecentSolvesWithDetails(PageRequest.of(0, FEED_SIZE))
                .stream()
                .map(s -> new FeedItemResponse(
                        s.getMember().getName(),
                        s.getMember().getAvatarUrl(),
                        s.getProblem().getBojProblemId(),
                        s.getProblem().getTitle(),
                        s.getProblem().getTier().name(),
                        s.getSolveType().name(),
                        s.getSolveTimeSeconds(),
                        s.getSolvedTime()
                ))
                .toList();

        return new FeedResponse(stats, recentFeeds);
    }
}
