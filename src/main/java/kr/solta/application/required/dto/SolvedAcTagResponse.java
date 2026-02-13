package kr.solta.application.required.dto;

import java.util.List;

public record SolvedAcTagResponse(
        String key,
        int bojTagId,
        List<SolvedAcDisplayName> displayNames
) {
}