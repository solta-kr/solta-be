package kr.solta.application.provided;

import static kr.solta.support.TestFixtures.createMember;
import static kr.solta.support.TestFixtures.createProblem;
import static kr.solta.support.TestFixtures.createReviewSchedule;
import static kr.solta.support.TestFixtures.createSolved;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import kr.solta.application.provided.request.AuthMember;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.ReviewScheduleRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.ReviewSchedule;
import kr.solta.domain.ReviewStatus;
import kr.solta.domain.SolveType;
import kr.solta.domain.Solved;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReviewScheduleUpdaterTest extends IntegrationTest {

    @Autowired private ReviewScheduleUpdater reviewScheduleUpdater;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProblemRepository problemRepository;
    @Autowired private SolvedRepository solvedRepository;
    @Autowired private ReviewScheduleRepository reviewScheduleRepository;

    private ReviewSchedule savedSchedule(Member member) {
        Problem problem = problemRepository.save(createProblem());
        Solved solved = solvedRepository.save(createSolved(1000, SolveType.SOLUTION, member, problem));
        return reviewScheduleRepository.save(createReviewSchedule(member, problem, solved));
    }

    @Test
    void 복습을_미루면_예정일이_3일_연장된다() {
        //given
        Member member = memberRepository.save(createMember());
        ReviewSchedule schedule = savedSchedule(member);
        LocalDate originalDate = schedule.getScheduledDate();

        //when
        reviewScheduleUpdater.skip(new AuthMember(member.getId()), schedule.getId());

        //then
        assertThat(schedule.getScheduledDate()).isEqualTo(originalDate.plusDays(3));
    }

    @Test
    void 본인이_아닌_사용자는_복습을_미룰_수_없다() {
        //given
        Member owner = memberRepository.save(createMember());
        Member other = memberRepository.save(createMember(2L, "other"));
        ReviewSchedule schedule = savedSchedule(owner);

        //when & then
        assertThatThrownBy(() -> reviewScheduleUpdater.skip(new AuthMember(other.getId()), schedule.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인의 복습 스케줄만 수정할 수 있습니다");
    }

    @Test
    void 복습_간격을_재설정할_수_있다() {
        //given
        Member member = memberRepository.save(createMember());
        ReviewSchedule schedule = savedSchedule(member);

        //when
        reviewScheduleUpdater.reschedule(new AuthMember(member.getId()), schedule.getId(), 7);

        //then
        assertThat(schedule.getIntervalDays()).isEqualTo(7);
        assertThat(schedule.getScheduledDate()).isEqualTo(LocalDate.now().plusDays(7));
    }

    @Test
    void 복습_간격을_0으로_재설정하면_DISMISSED된다() {
        //given
        Member member = memberRepository.save(createMember());
        ReviewSchedule schedule = savedSchedule(member);

        //when
        reviewScheduleUpdater.reschedule(new AuthMember(member.getId()), schedule.getId(), 0);

        //then
        assertThat(schedule.getStatus()).isEqualTo(ReviewStatus.DISMISSED);
    }

    @Test
    void 기본_복습_간격을_업데이트할_수_있다() {
        //given
        Member member = memberRepository.save(createMember());

        //when
        reviewScheduleUpdater.updateDefaultReviewInterval(new AuthMember(member.getId()), 7);

        //then
        assertThat(member.getEffectiveReviewInterval()).isEqualTo(7);
    }

    @Test
    void 존재하지_않는_스케줄을_미루면_예외가_발생한다() {
        //given
        Member member = memberRepository.save(createMember());

        //when & then
        assertThatThrownBy(() -> reviewScheduleUpdater.skip(new AuthMember(member.getId()), 999L))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("존재하지 않는 복습 스케줄입니다");
    }
}
