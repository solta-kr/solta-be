package kr.solta.application;

import static kr.solta.support.TestFixtures.createMember;
import static kr.solta.support.TestFixtures.createProblem;
import static kr.solta.support.TestFixtures.createReviewSchedule;
import static kr.solta.support.TestFixtures.createSolved;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import kr.solta.adapter.scheduler.ReviewDismissalScheduler;
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

class ReviewDismissalSchedulerTest extends IntegrationTest {

    @Autowired private ReviewDismissalScheduler reviewDismissalScheduler;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProblemRepository problemRepository;
    @Autowired private SolvedRepository solvedRepository;
    @Autowired private ReviewScheduleRepository reviewScheduleRepository;

    private ReviewSchedule savedSchedule(Member member, LocalDate scheduledFrom) {
        Problem problem = problemRepository.save(createProblem(
                "문제" + scheduledFrom, (long) (Math.random() * 100000)));
        Solved solved = solvedRepository.save(createSolved(1000, SolveType.SOLUTION, member, problem));
        return reviewScheduleRepository.save(createReviewSchedule(member, problem, solved, scheduledFrom));
    }

    @Test
    void 충분히_오래된_PENDING_스케줄은_DISMISSED된다() {
        //given
        // scheduledDate = baseDate + 3일, threshold = today - 14일
        // scheduledDate <= threshold 이려면 baseDate <= today - 17일
        Member member = memberRepository.save(createMember());
        ReviewSchedule old = savedSchedule(member, LocalDate.now().minusDays(17));

        //when
        reviewDismissalScheduler.dismissOverdueReviews();

        //then
        assertThat(old.getStatus()).isEqualTo(ReviewStatus.DISMISSED);
    }

    @Test
    void 아직_14일이_안_된_PENDING_스케줄은_DISMISSED되지_않는다() {
        //given
        // scheduledDate = today - 16 + 3 = today - 13 → threshold(today-14)보다 최신 → 유지
        Member member = memberRepository.save(createMember());
        ReviewSchedule recent = savedSchedule(member, LocalDate.now().minusDays(16));

        //when
        reviewDismissalScheduler.dismissOverdueReviews();

        //then
        assertThat(recent.getStatus()).isEqualTo(ReviewStatus.PENDING);
    }

    @Test
    void COMPLETED_스케줄은_DISMISSED되지_않는다() {
        //given
        Member member = memberRepository.save(createMember());
        ReviewSchedule schedule = savedSchedule(member, LocalDate.now().minusDays(20));
        schedule.complete();

        //when
        reviewDismissalScheduler.dismissOverdueReviews();

        //then
        assertThat(schedule.getStatus()).isEqualTo(ReviewStatus.COMPLETED);
    }

    @Test
    void 여러_스케줄을_한번에_DISMISSED_처리한다() {
        //given
        Member member = memberRepository.save(createMember());
        ReviewSchedule old1 = savedSchedule(member, LocalDate.now().minusDays(18));
        ReviewSchedule old2 = savedSchedule(member, LocalDate.now().minusDays(23));
        ReviewSchedule recent = savedSchedule(member, LocalDate.now().minusDays(8));

        //when
        reviewDismissalScheduler.dismissOverdueReviews();

        //then
        assertThat(old1.getStatus()).isEqualTo(ReviewStatus.DISMISSED);
        assertThat(old2.getStatus()).isEqualTo(ReviewStatus.DISMISSED);
        assertThat(recent.getStatus()).isEqualTo(ReviewStatus.PENDING);
    }
}
