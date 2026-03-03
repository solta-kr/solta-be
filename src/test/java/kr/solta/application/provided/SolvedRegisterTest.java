package kr.solta.application.provided;

import static kr.solta.support.TestFixtures.createMember;
import static kr.solta.support.TestFixtures.createProblem;
import static kr.solta.support.TestFixtures.createProblemTag;
import static kr.solta.support.TestFixtures.createTag;
import static org.assertj.core.api.Assertions.*;

import kr.solta.application.provided.request.AuthMember;
import kr.solta.application.provided.request.SolvedRegisterRequest;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.ProblemTagRepository;
import kr.solta.application.required.ReviewScheduleRepository;
import kr.solta.application.required.TagRepository;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.ReviewStatus;
import kr.solta.domain.SolveType;
import kr.solta.domain.Solved;
import kr.solta.domain.Tag;
import kr.solta.support.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SolvedRegisterTest extends IntegrationTest {

    @Autowired
    private SolvedRegister solvedRegister;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private ProblemTagRepository problemTagRepository;

    @Autowired
    private ReviewScheduleRepository reviewScheduleRepository;

    @Test
    void solved를_등록할_수_있다() {
        //given
        Member member = memberRepository.save(createMember());
        Tag tag = tagRepository.save(createTag());
        Problem problem = problemRepository.save(createProblem());
        problemTagRepository.save(createProblemTag(problem, tag));
        SolvedRegisterRequest solvedRegisterRequest = new SolvedRegisterRequest(
                SolveType.SELF,
                problem.getBojProblemId(),
                1200,
                null
        );

        //when
        Solved solved = solvedRegister.register(new AuthMember(member.getId()), solvedRegisterRequest);

        //then
        assertThat(solved)
                .extracting(Solved::getMember, Solved::getProblem, Solved::getSolveType, Solved::getSolveTimeSeconds)
                .containsExactly(member, problem, SolveType.SELF, 1200);
    }

    @Test
    void solved를_등록할때_인증된_사용자가_존재하지_않는다면_예외가_발생한다() {
        //given
        Problem problem = problemRepository.save(createProblem());
        SolvedRegisterRequest solvedRegisterRequest = new SolvedRegisterRequest(
                SolveType.SELF,
                problem.getBojProblemId(),
                1200,
                null
        );
        AuthMember notExistMember = new AuthMember(999L);

        //when & then
        assertThatThrownBy(() -> solvedRegister.register(notExistMember, solvedRegisterRequest))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("존재하지 않는 사용자입니다");
    }

    @Test
    void solved를_등록할때_해당_문제가_존재하지_않는다면_예외가_발생한다() {
        //given
        Member member = memberRepository.save(createMember());
        SolvedRegisterRequest solvedRegisterRequest = new SolvedRegisterRequest(
                SolveType.SELF,
                9999L,
                1200,
                null
        );

        //when & then
        assertThatThrownBy(() -> solvedRegister.register(new AuthMember(member.getId()), solvedRegisterRequest))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("백준 문제 번호: 9999 에 해당하는 문제가 존재하지 않습니다");
    }

    @Test
    void 메모와_함께_solved를_등록할_수_있다() {
        //given
        Member member = memberRepository.save(createMember());
        Tag tag = tagRepository.save(createTag());
        Problem problem = problemRepository.save(createProblem());
        problemTagRepository.save(createProblemTag(problem, tag));
        String memo = "그리디 접근으로 풀었다.";
        SolvedRegisterRequest solvedRegisterRequest = new SolvedRegisterRequest(
                SolveType.SELF,
                problem.getBojProblemId(),
                1200,
                memo
        );

        //when
        Solved solved = solvedRegister.register(new AuthMember(member.getId()), solvedRegisterRequest);

        //then
        assertThat(solved.getMemo()).isEqualTo(memo);
    }

    // ─── 복습 스케줄 트리거 ────────────────────────────────────────────────────

    @Test
    void SOLUTION으로_풀면_복습_스케줄이_생성된다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem());
        SolvedRegisterRequest request = new SolvedRegisterRequest(SolveType.SOLUTION, problem.getBojProblemId(), 1200, null);

        //when
        solvedRegister.register(new AuthMember(member.getId()), request);

        //then
        var schedules = reviewScheduleRepository.findAllPendingByMemberOrderByScheduledDateAsc(member);
        assertThat(schedules).hasSize(1);
        assertThat(schedules.get(0).getStatus()).isEqualTo(ReviewStatus.PENDING);
        assertThat(schedules.get(0).getRound()).isEqualTo(1);
    }

    @Test
    void SELF로_풀면_복습_스케줄이_생성되지_않는다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem());
        SolvedRegisterRequest request = new SolvedRegisterRequest(SolveType.SELF, problem.getBojProblemId(), 1200, null);

        //when
        solvedRegister.register(new AuthMember(member.getId()), request);

        //then
        var schedules = reviewScheduleRepository.findAllPendingByMemberOrderByScheduledDateAsc(member);
        assertThat(schedules).isEmpty();
    }

    @Test
    void PENDING_스케줄이_있을때_SOLUTION으로_다시_풀면_회차가_올라간다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem());
        SolvedRegisterRequest request = new SolvedRegisterRequest(SolveType.SOLUTION, problem.getBojProblemId(), 1200, null);
        solvedRegister.register(new AuthMember(member.getId()), request);

        //when
        solvedRegister.register(new AuthMember(member.getId()), request);

        //then
        var schedules = reviewScheduleRepository.findAllPendingByMemberOrderByScheduledDateAsc(member);
        assertThat(schedules).hasSize(1);
        assertThat(schedules.get(0).getRound()).isEqualTo(2);
        assertThat(schedules.get(0).getIntervalDays()).isEqualTo(6);
    }

    @Test
    void PENDING_스케줄이_있을때_SELF로_풀면_복습이_완료된다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem());
        solvedRegister.register(new AuthMember(member.getId()),
                new SolvedRegisterRequest(SolveType.SOLUTION, problem.getBojProblemId(), 1200, null));

        //when
        solvedRegister.register(new AuthMember(member.getId()),
                new SolvedRegisterRequest(SolveType.SELF, problem.getBojProblemId(), 900, null));

        //then
        var pending = reviewScheduleRepository.findAllPendingByMemberOrderByScheduledDateAsc(member);
        assertThat(pending).isEmpty();

        var completed = reviewScheduleRepository.findCompletedByMemberOrderByUpdatedAtDesc(
                member, org.springframework.data.domain.PageRequest.of(0, 10));
        assertThat(completed).hasSize(1);
        assertThat(completed.get(0).getStatus()).isEqualTo(ReviewStatus.COMPLETED);
    }
}
