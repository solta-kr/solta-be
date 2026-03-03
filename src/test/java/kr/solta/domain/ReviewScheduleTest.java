package kr.solta.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ReviewScheduleTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 3, 3);

    private Member member(Long githubId, String name) {
        return Member.create(githubId, name, "https://avatar.example.com/test.png");
    }

    private Problem problem() {
        return new Problem("DFS와 BFS", 1260L, Tier.S2);
    }

    private Solved solution(Member member, Problem problem) {
        return Solved.register(3600, SolveType.SOLUTION, member, problem, TODAY.atStartOfDay(), null);
    }

    private ReviewSchedule schedule(Member member) {
        Problem p = problem();
        return ReviewSchedule.create(member, p, solution(member, p), TODAY);
    }

    // ─── create ───────────────────────────────────────────────────────────────

    @Test
    void 기본_복습_간격으로_스케줄이_생성된다() {
        //given
        Member member = member(1L, "tester");
        Problem p = problem();

        //when
        ReviewSchedule schedule = ReviewSchedule.create(member, p, solution(member, p), TODAY);

        //then
        assertThat(schedule.getScheduledDate()).isEqualTo(TODAY.plusDays(3));
        assertThat(schedule.getRound()).isEqualTo(1);
        assertThat(schedule.getIntervalDays()).isEqualTo(3);
        assertThat(schedule.getStatus()).isEqualTo(ReviewStatus.PENDING);
    }

    @Test
    void 사용자_설정_복습_간격으로_스케줄이_생성된다() {
        //given
        Member member = member(1L, "tester");
        member.updateDefaultReviewInterval(7);
        Problem p = problem();

        //when
        ReviewSchedule schedule = ReviewSchedule.create(member, p, solution(member, p), TODAY);

        //then
        assertThat(schedule.getScheduledDate()).isEqualTo(TODAY.plusDays(7));
        assertThat(schedule.getIntervalDays()).isEqualTo(7);
    }

    // ─── advanceRound ─────────────────────────────────────────────────────────

    @Test
    void 복습_회차가_진행되면_간격이_2배가_된다() {
        //given
        Member member = member(1L, "tester");
        ReviewSchedule schedule = schedule(member);

        //when
        schedule.advanceRound(TODAY);

        //then
        assertThat(schedule.getIntervalDays()).isEqualTo(6);
        assertThat(schedule.getScheduledDate()).isEqualTo(TODAY.plusDays(6));
        assertThat(schedule.getRound()).isEqualTo(2);
    }

    @Test
    void 복습_간격은_최대_14일을_넘지_않는다() {
        //given
        Member member = member(1L, "tester");
        member.updateDefaultReviewInterval(7);
        ReviewSchedule schedule = schedule(member);

        //when
        schedule.advanceRound(TODAY);

        //then
        assertThat(schedule.getIntervalDays()).isEqualTo(14);
        assertThat(schedule.getScheduledDate()).isEqualTo(TODAY.plusDays(14));
    }

    @Test
    void PENDING이_아닌_스케줄은_회차를_진행할_수_없다() {
        //given
        Member member = member(1L, "tester");
        ReviewSchedule schedule = schedule(member);
        schedule.complete();

        //when & then
        assertThatThrownBy(() -> schedule.advanceRound(TODAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PENDING 상태의 복습 스케줄만 수정할 수 있습니다");
    }

    // ─── complete ─────────────────────────────────────────────────────────────

    @Test
    void 복습을_완료_처리할_수_있다() {
        //given
        Member member = member(1L, "tester");
        ReviewSchedule schedule = schedule(member);

        //when
        schedule.complete();

        //then
        assertThat(schedule.getStatus()).isEqualTo(ReviewStatus.COMPLETED);
    }

    @Test
    void PENDING이_아닌_스케줄은_완료_처리할_수_없다() {
        //given
        Member member = member(1L, "tester");
        ReviewSchedule schedule = schedule(member);
        schedule.complete();

        //when & then
        assertThatThrownBy(schedule::complete)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PENDING 상태의 복습 스케줄만 수정할 수 있습니다");
    }

    // ─── skip ─────────────────────────────────────────────────────────────────

    @Test
    void 복습을_미루면_예정일이_3일_연장된다() {
        //given
        Member member = member(1L, "tester");
        ReviewSchedule schedule = schedule(member);
        LocalDate originalDate = schedule.getScheduledDate();

        //when
        schedule.skip(member);

        //then
        assertThat(schedule.getScheduledDate()).isEqualTo(originalDate.plusDays(3));
        assertThat(schedule.getStatus()).isEqualTo(ReviewStatus.PENDING);
    }

    @Test
    void 본인이_아닌_사용자는_복습을_미룰_수_없다() {
        //given
        Member owner = member(1L, "owner");
        Member other = member(2L, "other");
        ReviewSchedule schedule = schedule(owner);

        //when & then
        assertThatThrownBy(() -> schedule.skip(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인의 복습 스케줄만 수정할 수 있습니다");
    }

    // ─── reschedule ───────────────────────────────────────────────────────────

    @Test
    void 복습_간격을_재설정할_수_있다() {
        //given
        Member member = member(1L, "tester");
        ReviewSchedule schedule = schedule(member);

        //when
        schedule.reschedule(member, 7, TODAY);

        //then
        assertThat(schedule.getIntervalDays()).isEqualTo(7);
        assertThat(schedule.getScheduledDate()).isEqualTo(TODAY.plusDays(7));
        assertThat(schedule.getStatus()).isEqualTo(ReviewStatus.PENDING);
    }

    @Test
    void 복습_간격을_0으로_설정하면_DISMISSED된다() {
        //given
        Member member = member(1L, "tester");
        ReviewSchedule schedule = schedule(member);

        //when
        schedule.reschedule(member, 0, TODAY);

        //then
        assertThat(schedule.getStatus()).isEqualTo(ReviewStatus.DISMISSED);
    }

    @Test
    void 본인이_아닌_사용자는_복습_간격을_재설정할_수_없다() {
        //given
        Member owner = member(1L, "owner");
        Member other = member(2L, "other");
        ReviewSchedule schedule = schedule(owner);

        //when & then
        assertThatThrownBy(() -> schedule.reschedule(other, 7, TODAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인의 복습 스케줄만 수정할 수 있습니다");
    }

    // ─── dismiss ──────────────────────────────────────────────────────────────

    @Test
    void 복습_스케줄을_dismiss_처리할_수_있다() {
        //given
        Member member = member(1L, "tester");
        ReviewSchedule schedule = schedule(member);

        //when
        schedule.dismiss();

        //then
        assertThat(schedule.getStatus()).isEqualTo(ReviewStatus.DISMISSED);
    }
}
