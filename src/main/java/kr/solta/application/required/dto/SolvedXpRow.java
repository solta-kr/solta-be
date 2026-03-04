package kr.solta.application.required.dto;

import kr.solta.domain.Solved;

public record SolvedXpRow(
        Solved solved,
        Integer xpAmount
) {
}
