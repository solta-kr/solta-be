package kr.solta.application.required.dto;

import java.util.List;

public record SolvedAcProblemResponse(
        int problemId,
        String titleKo,
        int level,
        List<SolvedAcTagResponse> tags
) {
}