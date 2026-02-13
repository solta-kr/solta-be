package kr.solta.application;

import java.util.ArrayList;
import java.util.List;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.SolvedAcClient;
import kr.solta.application.required.dto.SolvedAcProblemResponse;
import kr.solta.domain.Problem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemSyncService {

    private static final int BATCH_SIZE = 100;
    private static final int MAX_CONSECUTIVE_EMPTY = 5;
    private static final long RATE_LIMIT_DELAY_MS = 4000L;

    private final SolvedAcClient solvedAcClient;
    private final ProblemRepository problemRepository;
    private final ProblemSyncTransactionHandler transactionHandler;

    public void syncAll() {
        log.info("[ProblemSync] 문제 동기화 시작");

        int updatedCount = syncExistingProblems();
        log.info("[ProblemSync] 기존 문제 업데이트 완료: {}건", updatedCount);

        int newCount = discoverNewProblems();
        log.info("[ProblemSync] 신규 문제 등록 완료: {}건", newCount);

        log.info("[ProblemSync] 문제 동기화 완료 (업데이트: {}, 신규: {})", updatedCount, newCount);
    }

    private int syncExistingProblems() {
        int updatedCount = 0;
        long lastBojProblemId = 0;

        while (true) {
            List<Problem> problems = problemRepository.findAllAfterBojProblemId(
                    lastBojProblemId, PageRequest.of(0, BATCH_SIZE));

            if (problems.isEmpty()) {
                break;
            }

            List<Integer> bojIds = problems.stream()
                    .map(p -> (int) p.getBojProblemId())
                    .toList();

            try {
                List<SolvedAcProblemResponse> responses = solvedAcClient.lookupProblems(bojIds);
                updatedCount += transactionHandler.updateExistingBatch(problems, responses);
            } catch (Exception e) {
                log.error("[ProblemSync] 배치 업데이트 실패 (bojIds: {} ~ {}): {}",
                        bojIds.getFirst(), bojIds.getLast(), e.getMessage());
            }

            lastBojProblemId = problems.getLast().getBojProblemId();
            sleep();
        }

        return updatedCount;
    }

    private int discoverNewProblems() {
        long startId = problemRepository.findMaxBojProblemId() + 1;
        int newCount = 0;
        int consecutiveEmpty = 0;

        while (consecutiveEmpty < MAX_CONSECUTIVE_EMPTY) {
            List<Integer> candidateIds = new ArrayList<>();
            for (int i = 0; i < BATCH_SIZE; i++) {
                candidateIds.add((int) (startId + i));
            }

            try {
                List<SolvedAcProblemResponse> responses = solvedAcClient.lookupProblems(candidateIds);

                if (responses.isEmpty()) {
                    consecutiveEmpty++;
                } else {
                    consecutiveEmpty = 0;
                    newCount += transactionHandler.insertNewBatch(responses);
                }
            } catch (Exception e) {
                log.error("[ProblemSync] 신규 문제 탐색 실패 (startId: {}): {}", startId, e.getMessage());
                consecutiveEmpty++;
            }

            startId += BATCH_SIZE;
            sleep();
        }

        return newCount;
    }

    private void sleep() {
        try {
            Thread.sleep(RATE_LIMIT_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("[ProblemSync] 동기화 중 인터럽트 발생", e);
        }
    }
}
