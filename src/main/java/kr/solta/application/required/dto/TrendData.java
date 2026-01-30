package kr.solta.application.required.dto;

public record TrendData(
        String date,
        Double averageSeconds,
        Long solvedCount
) {
}