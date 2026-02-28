package kr.solta.adapter.webapi.response;

import java.util.List;
import kr.solta.domain.TagWeakness;
import kr.solta.domain.WeaknessLevel;

public record TagWeaknessResponse(
        String key,
        String korName,
        long totalCount,
        int selfSolveRate,
        Double avgSolveSeconds,
        Double timeRatio,
        double weaknessScore,
        WeaknessLevel weaknessLevel,
        double confidence,
        double finalScore
) {
    public static TagWeaknessResponse from(TagWeakness domain) {
        return new TagWeaknessResponse(
                domain.getTag().getKey(),
                domain.getTag().getKorName(),
                domain.getTotalCount(),
                domain.getSelfSolveRate(),
                domain.getAvgSolveSeconds(),
                domain.getTimeRatio(),
                domain.getWeaknessScore(),
                domain.getWeaknessLevel(),
                domain.getConfidence(),
                domain.getFinalScore()
        );
    }

    public static List<TagWeaknessResponse> from(List<TagWeakness> domains) {
        return domains.stream().map(TagWeaknessResponse::from).toList();
    }
}
