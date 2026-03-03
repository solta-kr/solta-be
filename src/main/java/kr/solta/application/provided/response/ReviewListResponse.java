package kr.solta.application.provided.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import kr.solta.domain.ReviewSchedule;
import kr.solta.domain.Tier;

public record ReviewListResponse(
        int overdueCount,
        List<ReviewItem> reviews
) {
    public record ReviewItem(
            long id,
            LocalDate scheduledDate,
            boolean isOverdue,
            int round,
            ProblemSummary problem,
            LocalDateTime originSolvedAt
    ) {
        public record ProblemSummary(
                long bojProblemId,
                String title,
                Tier tier,
                List<String> tags
        ) {
        }
    }

    public static ReviewListResponse of(final List<ReviewSchedule> schedules, final List<List<String>> tagsList, final LocalDate today) {
        List<ReviewItem> items = new java.util.ArrayList<>();
        for (int i = 0; i < schedules.size(); i++) {
            ReviewSchedule schedule = schedules.get(i);
            List<String> tags = tagsList.get(i);
            boolean isOverdue = schedule.getScheduledDate().isBefore(today);
            items.add(new ReviewItem(
                    schedule.getId(),
                    schedule.getScheduledDate(),
                    isOverdue,
                    schedule.getRound(),
                    new ReviewItem.ProblemSummary(
                            schedule.getProblem().getBojProblemId(),
                            schedule.getProblem().getTitle(),
                            schedule.getProblem().getTier(),
                            tags
                    ),
                    schedule.getOriginSolved().getSolvedTime()
            ));
        }

        long overdueCount = items.stream().filter(ReviewItem::isOverdue).count();
        return new ReviewListResponse((int) overdueCount, items);
    }
}
