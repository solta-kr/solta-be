package kr.solta.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ActivityCalendarTest {

    @Test
    void 오늘_풀이가_있으면_오늘부터_연속_카운트한다() {
        //given
        LocalDate today = LocalDate.now();
        List<LocalDate> dates = List.of(today.minusDays(2), today.minusDays(1), today);
        ActivityCalendar calendar = new ActivityCalendar(dates);

        //when & then
        assertThat(calendar.currentStreak()).isEqualTo(3);
    }

    @Test
    void 오늘_풀이가_없으면_어제부터_연속_카운트한다() {
        //given
        LocalDate today = LocalDate.now();
        List<LocalDate> dates = List.of(today.minusDays(3), today.minusDays(2), today.minusDays(1));
        ActivityCalendar calendar = new ActivityCalendar(dates);

        //when & then
        assertThat(calendar.currentStreak()).isEqualTo(3);
    }

    @Test
    void 오늘도_어제도_풀이가_없으면_스트릭은_0이다() {
        //given
        LocalDate today = LocalDate.now();
        List<LocalDate> dates = List.of(today.minusDays(5), today.minusDays(4));
        ActivityCalendar calendar = new ActivityCalendar(dates);

        //when & then
        assertThat(calendar.currentStreak()).isEqualTo(0);
    }

    @Test
    void 풀이_기록이_없으면_스트릭은_모두_0이다() {
        //given
        ActivityCalendar calendar = new ActivityCalendar(List.of());

        //when & then
        assertSoftly(softly -> {
            softly.assertThat(calendar.currentStreak()).isEqualTo(0);
            softly.assertThat(calendar.longestStreak()).isEqualTo(0);
        });
    }

    @Test
    void 최장_스트릭을_정확히_계산한다() {
        //given
        List<LocalDate> dates = List.of(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 2),
                LocalDate.of(2025, 1, 3),  // 3일 연속
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 11),
                LocalDate.of(2025, 1, 12),
                LocalDate.of(2025, 1, 13),
                LocalDate.of(2025, 1, 14)  // 5일 연속
        );
        ActivityCalendar calendar = new ActivityCalendar(dates);

        //when & then
        assertThat(calendar.longestStreak()).isEqualTo(5);
    }

    @Test
    void 하루만_풀었어도_최장_스트릭은_1이다() {
        //given
        ActivityCalendar calendar = new ActivityCalendar(List.of(LocalDate.of(2025, 3, 15)));

        //when & then
        assertThat(calendar.longestStreak()).isEqualTo(1);
    }

    @Test
    void 모두_연속된_날이면_전체_기간이_최장_스트릭이다() {
        //given
        List<LocalDate> dates = List.of(
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 2, 2),
                LocalDate.of(2025, 2, 3),
                LocalDate.of(2025, 2, 4),
                LocalDate.of(2025, 2, 5)
        );
        ActivityCalendar calendar = new ActivityCalendar(dates);

        //when & then
        assertThat(calendar.longestStreak()).isEqualTo(5);
    }
}
