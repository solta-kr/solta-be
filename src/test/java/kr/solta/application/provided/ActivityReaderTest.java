package kr.solta.application.provided;

import static kr.solta.support.TestFixtures.createMember;
import static kr.solta.support.TestFixtures.createProblem;
import static kr.solta.support.TestFixtures.createSolved;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDate;
import java.time.LocalDateTime;
import kr.solta.application.provided.response.ActivityHeatmapResponse;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.SolveType;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ActivityReaderTest extends IntegrationTest {

    @Autowired
    private ActivityReader activityReader;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SolvedRepository solvedRepository;

    @Test
    void 날짜_범위_내_활동이_있는_날만_반환한다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));
        Problem problem = problemRepository.save(createProblem("문제", 1000L));

        solvedRepository.save(createSolved(3600, SolveType.SELF, member, problem,
                LocalDateTime.of(2025, 3, 1, 10, 0)));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem,
                LocalDateTime.of(2025, 3, 3, 14, 0)));
        solvedRepository.save(createSolved(1200, SolveType.SELF, member, problem,
                LocalDateTime.of(2025, 2, 28, 9, 0)));  // 범위 밖

        //when
        ActivityHeatmapResponse response = activityReader.getActivityHeatmap(
                "testUser",
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 31)
        );

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.activities()).hasSize(2);
            softly.assertThat(response.activities().get(0).date()).isEqualTo("2025-03-01");
            softly.assertThat(response.activities().get(0).count()).isEqualTo(1);
            softly.assertThat(response.activities().get(0).totalSeconds()).isEqualTo(3600);
            softly.assertThat(response.activities().get(1).date()).isEqualTo("2025-03-03");
        });
    }

    @Test
    void 같은_날_여러_풀이는_집계된다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));
        Problem problem1 = problemRepository.save(createProblem("문제1", 1000L));
        Problem problem2 = problemRepository.save(createProblem("문제2", 1001L));

        LocalDateTime sameDay = LocalDateTime.of(2025, 5, 10, 10, 0);
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem1, sameDay));
        solvedRepository.save(createSolved(2400, SolveType.SELF, member, problem2, sameDay.plusHours(2)));

        //when
        ActivityHeatmapResponse response = activityReader.getActivityHeatmap(
                "testUser",
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 31)
        );

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.activities()).hasSize(1);
            softly.assertThat(response.activities().get(0).count()).isEqualTo(2);
            softly.assertThat(response.activities().get(0).totalSeconds()).isEqualTo(4200);
        });
    }

    @Test
    void 오늘까지_연속으로_풀었으면_currentStreak를_계산한다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));
        Problem problem = problemRepository.save(createProblem("문제", 1000L));

        LocalDate today = LocalDate.now();
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem, today.minusDays(2).atStartOfDay()));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem, today.minusDays(1).atStartOfDay()));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem, today.atStartOfDay()));

        //when
        ActivityHeatmapResponse response = activityReader.getActivityHeatmap(
                "testUser",
                today.minusDays(30),
                today
        );

        //then
        assertThat(response.currentStreak()).isEqualTo(3);
    }

    @Test
    void 최장_스트릭을_계산한다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));
        Problem problem = problemRepository.save(createProblem("문제", 1000L));

        // 3일 연속
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem, LocalDateTime.of(2025, 1, 1, 10, 0)));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem, LocalDateTime.of(2025, 1, 2, 10, 0)));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem, LocalDateTime.of(2025, 1, 3, 10, 0)));
        // 5일 연속
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem, LocalDateTime.of(2025, 2, 1, 10, 0)));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem, LocalDateTime.of(2025, 2, 2, 10, 0)));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem, LocalDateTime.of(2025, 2, 3, 10, 0)));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem, LocalDateTime.of(2025, 2, 4, 10, 0)));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem, LocalDateTime.of(2025, 2, 5, 10, 0)));

        //when
        ActivityHeatmapResponse response = activityReader.getActivityHeatmap(
                "testUser",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
        );

        //then
        assertThat(response.longestStreak()).isEqualTo(5);
    }

    @Test
    void 활동이_없는_경우_빈_목록과_스트릭_0을_반환한다() {
        //given
        memberRepository.save(createMember(1L, "testUser"));

        //when
        ActivityHeatmapResponse response = activityReader.getActivityHeatmap(
                "testUser",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
        );

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.activities()).isEmpty();
            softly.assertThat(response.currentStreak()).isEqualTo(0);
            softly.assertThat(response.longestStreak()).isEqualTo(0);
        });
    }

    @Test
    void 존재하지_않는_사용자_조회시_예외가_발생한다() {
        //when & then
        assertThatThrownBy(() -> activityReader.getActivityHeatmap(
                "notExist",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다");
    }
}
