package kr.solta.application.provided.response;

public record StreakResponse(
        int currentStreak,
        int longestStreak
) {

    public static StreakResponse of(final int currentStreak, final int longestStreak) {
        return new StreakResponse(currentStreak, longestStreak);
    }

    public static StreakResponse empty() {
        return new StreakResponse(0, 0);
    }
}
