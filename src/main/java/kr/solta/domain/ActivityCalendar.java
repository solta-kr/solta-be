package kr.solta.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActivityCalendar {

    private final List<LocalDate> sortedDates;

    public ActivityCalendar(final List<LocalDate> sortedDates) {
        this.sortedDates = sortedDates;
    }

    public int currentStreak() {
        Set<LocalDate> dateSet = new HashSet<>(sortedDates);
        LocalDate today = LocalDate.now();
        LocalDate startDate = dateSet.contains(today) ? today : today.minusDays(1);
        int streak = 0;
        LocalDate cursor = startDate;
        while (dateSet.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    public int longestStreak() {
        if (sortedDates.isEmpty()) return 0;
        int longest = 1;
        int current = 1;
        for (int i = 1; i < sortedDates.size(); i++) {
            if (sortedDates.get(i).minusDays(1).equals(sortedDates.get(i - 1))) {
                current++;
                if (current > longest) longest = current;
            } else {
                current = 1;
            }
        }
        return longest;
    }
}
