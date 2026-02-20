package kr.solta.application.required.dto;

public record BadgeSummaryStats(
        long totalSeconds,
        double avgSeconds,
        long solveCount
) {
}
