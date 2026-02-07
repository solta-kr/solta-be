package kr.solta.application.required.dto;

public record ProblemSolvedStats(
        long totalSolvedCount,
        long independentSolvedCount,
        Double averageSolveTimeSeconds,
        Integer shortestSolveTimeSeconds
) {
}