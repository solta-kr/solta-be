package kr.solta.application.provided.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import kr.solta.domain.ReviewSchedule;
import kr.solta.domain.Tier;

public record ReviewHistoryResponse(
        List<HistoryItem> histories
) {
    public record HistoryItem(
            long id,
            LocalDate scheduledDate,
            LocalDateTime completedAt,
            int round,
            ProblemSummary problem
    ) {
        public record ProblemSummary(
                long bojProblemId,
                String title,
                Tier tier,
                List<String> tags
        ) {
        }
    }

    public static ReviewHistoryResponse of(final List<ReviewSchedule> schedules, final List<List<String>> tagsList) {
        List<HistoryItem> items = new ArrayList<>();
        for (int i = 0; i < schedules.size(); i++) {
            ReviewSchedule schedule = schedules.get(i);
            List<String> tags = tagsList.get(i);
            items.add(new HistoryItem(
                    schedule.getId(),
                    schedule.getScheduledDate(),
                    schedule.getUpdatedAt(),
                    schedule.getRound(),
                    new HistoryItem.ProblemSummary(
                            schedule.getProblem().getBojProblemId(),
                            schedule.getProblem().getTitle(),
                            schedule.getProblem().getTier(),
                            tags
                    )
            ));
        }
        return new ReviewHistoryResponse(items);
    }
}
