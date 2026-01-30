package kr.solta.application.provided.response;

public record TrendPoint(
        String date,
        Double averageSeconds,
        Long solvedCount
) {
}