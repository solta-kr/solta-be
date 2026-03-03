package kr.solta.adapter.scheduler;

import java.time.LocalDate;
import java.util.List;
import kr.solta.application.required.ReviewScheduleRepository;
import kr.solta.domain.ReviewSchedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewDismissalScheduler {

    private final ReviewScheduleRepository reviewScheduleRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void dismissOverdueReviews() {
        try {
            LocalDate threshold = LocalDate.now().minusDays(14);
            List<ReviewSchedule> overdue = reviewScheduleRepository.findAllPendingOlderThanOrEqual(threshold);
            overdue.forEach(ReviewSchedule::dismiss);
            log.info("[ReviewDismissalScheduler] {}개 복습 스케줄 DISMISSED 처리", overdue.size());
        } catch (Exception e) {
            log.error("[ReviewDismissalScheduler] 자동 DISMISSED 처리 실패", e);
        }
    }
}
