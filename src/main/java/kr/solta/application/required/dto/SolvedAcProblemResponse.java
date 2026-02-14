package kr.solta.application.required.dto;

import java.util.List;

public record SolvedAcProblemResponse(
        long problemId,
        String titleKo,
        int level,
        List<SolvedAcTagResponse> tags
) {
}
