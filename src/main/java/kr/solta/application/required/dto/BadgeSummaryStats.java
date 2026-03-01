package kr.solta.application.required.dto;


public record BadgeSummaryStats(
        long totalSeconds,
        double avgSeconds,
        Integer selfSolveRateRaw
) {
    public int selfSolveRate() {
        return selfSolveRateRaw != null ? selfSolveRateRaw : 0;
    }
}
