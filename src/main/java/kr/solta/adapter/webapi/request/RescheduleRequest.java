package kr.solta.adapter.webapi.request;

import jakarta.validation.constraints.Min;

public record RescheduleRequest(
        @Min(0) int interval
) {
}
