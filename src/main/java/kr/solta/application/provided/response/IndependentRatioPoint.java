package kr.solta.application.provided.response;

public record IndependentRatioPoint(
        String date,
        Long independentCount,
        Long totalCount
) {
}