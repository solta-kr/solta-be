package kr.solta.application.provided;

import kr.solta.application.provided.response.StreakResponse;

public interface StreakReader {

    StreakResponse getStreak(final String name);
}
