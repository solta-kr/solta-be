package kr.solta.domain;

import lombok.Getter;

@Getter
public enum AggregationType {
    DAILY("%Y-%m-%d"),
    MONTHLY("%Y-%m");

    private final String dateFormat;

    AggregationType(final String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
