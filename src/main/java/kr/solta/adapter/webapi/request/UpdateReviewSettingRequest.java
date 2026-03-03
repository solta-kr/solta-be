package kr.solta.adapter.webapi.request;

import jakarta.validation.constraints.Min;

public record UpdateReviewSettingRequest(
        @Min(1) int defaultReviewInterval
) {
}
