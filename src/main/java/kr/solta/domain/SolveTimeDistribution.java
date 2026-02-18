package kr.solta.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class SolveTimeDistribution {

    private static final int MIN_BUCKET_SIZE = 300;
    private static final int BUCKET_ALIGN_UNIT = 300;
    private static final int MAX_BUCKET_COUNT = 20;

    private final int maxSolveTimeSeconds;
    private final int bucketSize;
    private List<Bucket> buckets;

    public SolveTimeDistribution(int maxSolveTimeSeconds) {
        this.maxSolveTimeSeconds = maxSolveTimeSeconds;
        this.bucketSize = calculateBucketSize(maxSolveTimeSeconds);
        this.buckets = Collections.emptyList();
    }

    public static SolveTimeDistribution empty() {
        return new SolveTimeDistribution(0);
    }

    public void fillBuckets(Map<Long, Long> bucketCountMap) {
        if (maxSolveTimeSeconds == 0) {
            return;
        }
        long totalBuckets = (maxSolveTimeSeconds - 1) / bucketSize + 1;
        List<Bucket> result = new ArrayList<>();
        for (long i = 0; i < totalBuckets; i++) {
            int rangeStart = (int) (i * bucketSize + 1);
            int rangeEnd = (int) ((i + 1) * bucketSize);
            result.add(new Bucket(rangeStart, rangeEnd, bucketCountMap.getOrDefault(i, 0L)));
        }
        this.buckets = result;
    }

    public double calculateTopPercent(long totalCount, long slowerCount) {
        if (totalCount == 0) {
            return 0.0;
        }
        return Math.round((double) (totalCount - slowerCount) / totalCount * 1000.0) / 10.0;
    }

    public List<Bucket> getBuckets() {
        return Collections.unmodifiableList(buckets);
    }

    private int calculateBucketSize(int maxTime) {
        if (maxTime == 0) {
            return MIN_BUCKET_SIZE;
        }
        double rawBucketSize = Math.ceil((double) maxTime / MAX_BUCKET_COUNT);
        int aligned = (int) (Math.ceil(rawBucketSize / BUCKET_ALIGN_UNIT) * BUCKET_ALIGN_UNIT);
        return Math.max(aligned, MIN_BUCKET_SIZE);
    }

    public record Bucket(int rangeStart, int rangeEnd, long count) {
    }
}
