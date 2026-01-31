package kr.solta.domain;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public enum SolvedPeriod {
    WEEK("1주", 7, AggregationType.DAILY),
    MONTH("1개월", 30, AggregationType.DAILY),
    MONTH_3("3개월", 90, AggregationType.MONTHLY),
    MONTH_6("6개월", 180, AggregationType.MONTHLY),
    ALL("전체", null, AggregationType.MONTHLY);

    private final String label;
    private final Integer days;
    private final AggregationType aggregationType;

    SolvedPeriod(final String label, final Integer days, final AggregationType aggregationType) {
        this.label = label;
        this.days = days;
        this.aggregationType = aggregationType;
    }

    public LocalDateTime getStartDate(final LocalDateTime now) {
        if (days == null) {
            return null;
        }

        return now.minusDays(days);
    }
}
