package kr.solta.application.required.dto;

public record AllSolvedAverage(
        Long solvedCount,
        Long totalSolvedTime,
        Double totalSolvedAverageTime
) {
}
