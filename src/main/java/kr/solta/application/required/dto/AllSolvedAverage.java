package kr.solta.application.required.dto;

public record AllSolvedAverage(
        Long solvedCount,
        long totalSolvedTime,
        Double totalSolvedAverageTime
) {
}
