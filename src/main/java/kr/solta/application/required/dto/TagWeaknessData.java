package kr.solta.application.required.dto;

import kr.solta.domain.Tag;

public record TagWeaknessData(
        Tag tag,
        Long totalCount,
        Long selfCount,
        Double avgSolveSeconds
) {}
