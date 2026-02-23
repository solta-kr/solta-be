package kr.solta.application.required.dto;

public record DailyActivity(String date, Long count, Long totalSeconds, Long independentCount) {
}
