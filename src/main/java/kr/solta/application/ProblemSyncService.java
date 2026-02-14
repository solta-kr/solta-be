package kr.solta.application;

import java.util.List;
import java.util.stream.LongStream;
import kr.solta.application.provided.ProblemSynchronizer;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.SolvedAcClient;
import kr.solta.application.required.SolvedAcRateLimiter;
import kr.solta.application.required.dto.SolvedAcProblemResponse;
import kr.solta.domain.Problem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemSyncService implements ProblemSynchronizer {

    private static final int BATCH_SIZE = 100;

    private final SolvedAcClient solvedAcClient;
    private final SolvedAcRateLimiter solvedAcRateLimiter;
    private final ProblemRepository problemRepository;
    private final ProblemSyncTransactionHandler transactionHandler;

    @Override
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
        List<Problem> problems;

        while (!(problems = findNextBatch(lastBojProblemId)).isEmpty()) {
            try {
                List<SolvedAcProblemResponse> responses = solvedAcClient.lookupProblems(toBojIds(problems));
                updatedCount += transactionHandler.updateExistingBatch(problems, responses);
            } catch (Exception e) {
                log.error("[ProblemSync] 배치 업데이트 실패 (bojIds: {} ~ {}): {}",
                        problems.getFirst().getBojProblemId(), problems.getLast().getBojProblemId(), e.getMessage());
            }

            lastBojProblemId = problems.getLast().getBojProblemId();
            solvedAcRateLimiter.waitForNext();
        }

        return updatedCount;
    }

    private int discoverNewProblems() {
        long startId = problemRepository.findMaxBojProblemId() + 1;
        int newCount = 0;
        List<SolvedAcProblemResponse> responses;

        while (!(responses = lookupCandidates(startId)).isEmpty()) {
            newCount += transactionHandler.insertNewBatch(responses);
            startId += BATCH_SIZE;
            solvedAcRateLimiter.waitForNext();
        }

        return newCount;
    }

    private List<SolvedAcProblemResponse> lookupCandidates(final long startId) {
        List<Long> candidateIds = LongStream.range(startId, startId + BATCH_SIZE)
                .boxed()
                .toList();

        try {
            return solvedAcClient.lookupProblems(candidateIds);
        } catch (Exception e) {
            log.error("[ProblemSync] 신규 문제 탐색 실패 (startId: {}): {}", startId, e.getMessage());
            return List.of();
        }
    }

    private List<Problem> findNextBatch(final long lastBojProblemId) {
        return problemRepository.findAllAfterBojProblemId(lastBojProblemId, PageRequest.of(0, BATCH_SIZE));
    }

    private List<Long> toBojIds(final List<Problem> problems) {
        return problems.stream()
                .map(Problem::getBojProblemId)
                .toList();
    }
}
