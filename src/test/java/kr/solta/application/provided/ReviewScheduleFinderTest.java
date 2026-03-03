package kr.solta.application.provided;

import static kr.solta.support.TestFixtures.createMember;
import static kr.solta.support.TestFixtures.createProblem;
import static kr.solta.support.TestFixtures.createReviewSchedule;
import static kr.solta.support.TestFixtures.createSolved;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import kr.solta.application.provided.response.ReviewHistoryResponse;
import kr.solta.application.provided.response.ReviewListResponse;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.ReviewScheduleRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.ReviewSchedule;
import kr.solta.domain.SolveType;
import kr.solta.domain.Solved;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReviewScheduleFinderTest extends IntegrationTest {

    @Autowired private ReviewScheduleFinder reviewScheduleFinder;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProblemRepository problemRepository;
    @Autowired private SolvedRepository solvedRepository;
    @Autowired private ReviewScheduleRepository reviewScheduleRepository;

    @Test
    void PENDING_복습_목록을_예정일_오름차순으로_조회한다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem p1 = problemRepository.save(createProblem("문제A", 1001L));
        Problem p2 = problemRepository.save(createProblem("문제B", 1002L));
        Solved s1 = solvedRepository.save(createSolved(1000, SolveType.SOLUTION, member, p1));
        Solved s2 = solvedRepository.save(createSolved(1000, SolveType.SOLUTION, member, p2));

        LocalDate today = LocalDate.now();
        ReviewSchedule near = reviewScheduleRepository.save(createReviewSchedule(member, p1, s1, today.minusDays(1)));
        ReviewSchedule far = reviewScheduleRepository.save(createReviewSchedule(member, p2, s2, today.plusDays(3)));

        //when
        ReviewListResponse result = reviewScheduleFinder.findReviews(member.getName());

        //then
        assertThat(result.reviews()).hasSize(2);
        assertThat(result.reviews().get(0).id()).isEqualTo(near.getId());
        assertThat(result.reviews().get(1).id()).isEqualTo(far.getId());
    }

    @Test
    void 밀린_복습은_isOverdue가_true로_반환된다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem());
        Solved solved = solvedRepository.save(createSolved(1000, SolveType.SOLUTION, member, problem));
        reviewScheduleRepository.save(createReviewSchedule(member, problem, solved, LocalDate.now().minusDays(5)));

        //when
        ReviewListResponse result = reviewScheduleFinder.findReviews(member.getName());

        //then
        assertThat(result.reviews()).hasSize(1);
        assertThat(result.reviews().get(0).isOverdue()).isTrue();
        assertThat(result.overdueCount()).isEqualTo(1);
    }

    @Test
    void COMPLETED_스케줄은_PENDING_목록에_포함되지_않는다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem());
        Solved solved = solvedRepository.save(createSolved(1000, SolveType.SOLUTION, member, problem));
        ReviewSchedule schedule = reviewScheduleRepository.save(createReviewSchedule(member, problem, solved));
        schedule.complete();

        //when
        ReviewListResponse result = reviewScheduleFinder.findReviews(member.getName());

        //then
        assertThat(result.reviews()).isEmpty();
        assertThat(result.overdueCount()).isEqualTo(0);
    }

    @Test
    void 다른_사용자의_복습도_이름으로_조회할_수_있다() {
        //given
        Member other = memberRepository.save(createMember(2L, "other"));
        Problem problem = problemRepository.save(createProblem());
        Solved solved = solvedRepository.save(createSolved(1000, SolveType.SOLUTION, other, problem));
        reviewScheduleRepository.save(createReviewSchedule(other, problem, solved));

        //when
        ReviewListResponse result = reviewScheduleFinder.findReviews(other.getName());

        //then
        assertThat(result.reviews()).hasSize(1);
    }

    @Test
    void 존재하지_않는_사용자의_복습을_조회하면_예외가_발생한다() {
        //when & then
        assertThatThrownBy(() -> reviewScheduleFinder.findReviews("없는사용자"))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("존재하지 않는 사용자입니다");
    }

    @Test
    void 완료된_복습_이력을_조회한다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem p1 = problemRepository.save(createProblem("문제A", 1001L));
        Problem p2 = problemRepository.save(createProblem("문제B", 1002L));
        Solved s1 = solvedRepository.save(createSolved(1000, SolveType.SOLUTION, member, p1));
        Solved s2 = solvedRepository.save(createSolved(1000, SolveType.SOLUTION, member, p2));

        ReviewSchedule c1 = reviewScheduleRepository.save(createReviewSchedule(member, p1, s1));
        ReviewSchedule c2 = reviewScheduleRepository.save(createReviewSchedule(member, p2, s2));
        c1.complete();
        c2.complete();

        //when
        ReviewHistoryResponse result = reviewScheduleFinder.findCompletedReviews(member.getName());

        //then
        assertThat(result.histories()).hasSize(2);
    }

    @Test
    void PENDING_스케줄은_완료_이력에_포함되지_않는다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem());
        Solved solved = solvedRepository.save(createSolved(1000, SolveType.SOLUTION, member, problem));
        reviewScheduleRepository.save(createReviewSchedule(member, problem, solved));

        //when
        ReviewHistoryResponse result = reviewScheduleFinder.findCompletedReviews(member.getName());

        //then
        assertThat(result.histories()).isEmpty();
    }
}
