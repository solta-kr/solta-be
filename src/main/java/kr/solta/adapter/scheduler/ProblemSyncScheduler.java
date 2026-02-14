package kr.solta.adapter.scheduler;

import kr.solta.application.provided.ProblemSynchronizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProblemSyncScheduler {

    private final ProblemSynchronizer problemSynchronizer;

    @Scheduled(cron = "0 0 6 * * *")
    public void scheduleProblemSync() {
        try {
            problemSynchronizer.syncAll();
        } catch (Exception e) {
            log.error("[ProblemSyncScheduler] 문제 동기화 스케줄 실행 실패", e);
        }
    }
}