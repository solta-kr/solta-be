package kr.solta.adapter.solvedac;

import java.util.ArrayDeque;
import java.util.Deque;
import kr.solta.application.required.SolvedAcRateLimiter;
import org.springframework.stereotype.Component;

@Component
public class SolvedAcSleepRateLimiter implements SolvedAcRateLimiter {

    private static final int MAX_CALLS = 256;
    private static final long WINDOW_MS = 15 * 60 * 1000L;

    private final Deque<Long> callTimestamps = new ArrayDeque<>();

    @Override
    public void waitForNext() {
        long now = System.currentTimeMillis();
        removeExpired(now);

        if (callTimestamps.size() >= MAX_CALLS) {
            long oldestTimestamp = callTimestamps.peekFirst();
            long waitMs = oldestTimestamp + WINDOW_MS - now;
            if (waitMs > 0) {
                sleep(waitMs);
            }
            removeExpired(System.currentTimeMillis());
        }

        callTimestamps.addLast(System.currentTimeMillis());
    }

    private void removeExpired(final long now) {
        while (!callTimestamps.isEmpty() && callTimestamps.peekFirst() + WINDOW_MS <= now) {
            callTimestamps.pollFirst();
        }
    }

    private void sleep(final long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("[SolvedAcRateLimiter] 대기 중 인터럽트 발생", e);
        }
    }
}