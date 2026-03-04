package kr.solta.application.provided.response;

import java.time.LocalDateTime;
import java.util.List;
import kr.solta.domain.XpHistory;
import kr.solta.domain.XpSolveType;

public record XpHistoryResponse(
        int periodXp,
        List<XpHistoryItemResponse> history
) {
    public record XpHistoryItemResponse(
            long solvedId,
            int xpAmount,
            XpSolveType solveType,
            double tierWeight,
            double streakBonus,
            LocalDateTime createdAt
    ) {
        public static XpHistoryItemResponse from(final XpHistory h) {
            return new XpHistoryItemResponse(
                    h.getSolved().getId(),
                    h.getXpAmount(),
                    h.getSolveType(),
                    h.getTierWeight().doubleValue(),
                    h.getStreakBonus().doubleValue(),
                    h.getCreatedAt()
            );
        }
    }

    public static XpHistoryResponse of(final List<XpHistory> histories) {
        int periodXp = histories.stream().mapToInt(XpHistory::getXpAmount).sum();
        List<XpHistoryItemResponse> items = histories.stream()
                .map(XpHistoryItemResponse::from)
                .toList();
        return new XpHistoryResponse(periodXp, items);
    }
}
