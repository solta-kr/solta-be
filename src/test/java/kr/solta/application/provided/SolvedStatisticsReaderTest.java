package kr.solta.application.provided;

import static kr.solta.support.TestFixtures.createMember;
import static kr.solta.support.TestFixtures.createProblem;
import static kr.solta.support.TestFixtures.createSolved;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDateTime;
import kr.solta.application.provided.request.TagKey;
import kr.solta.application.provided.response.IndependentRatioPoint;
import kr.solta.application.provided.response.IndependentSolveTrendsResponse;
import kr.solta.application.provided.response.SolveTimeTrendsResponse;
import kr.solta.application.provided.response.TrendPoint;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.ProblemTagRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.application.required.TagRepository;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.ProblemTag;
import kr.solta.domain.SolveType;
import kr.solta.domain.SolvedPeriod;
import kr.solta.domain.Tag;
import kr.solta.domain.Tier;
import kr.solta.domain.TierGroup;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SolvedStatisticsReaderTest extends IntegrationTest {

    @Autowired
    private SolvedStatisticsReader solvedStatisticsReader;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SolvedRepository solvedRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ProblemTagRepository problemTagRepository;

    @Test
    void 평균_풀이_시간_추이를_날짜별로_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));
        LocalDateTime now = LocalDateTime.of(2024, 1, 10, 12, 0);

        Problem problem1 = problemRepository.save(createProblem("문제1", 1000L, Tier.B1));
        Problem problem2 = problemRepository.save(createProblem("문제2", 1001L, Tier.B2));
        Problem problem3 = problemRepository.save(createProblem("문제3", 1002L, Tier.S3));
        Problem problem4 = problemRepository.save(createProblem("문제4", 1003L, Tier.S2));
        Problem problem5 = problemRepository.save(createProblem("문제5", 1004L, Tier.G1));

        // 1월 8일: 2문제 풀이 (3600초, 1800초)
        solvedRepository.save(createSolved(3600, SolveType.SELF, member, problem1, now.minusDays(2)));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem2, now.minusDays(2)));

        // 1월 9일: 1문제 풀이 (5400초)
        solvedRepository.save(createSolved(5400, SolveType.SOLUTION, member, problem3, now.minusDays(1)));

        // 1월 10일: 2문제 풀이 (2400초, 3000초)
        solvedRepository.save(createSolved(2400, SolveType.SELF, member, problem4, now));
        solvedRepository.save(createSolved(3000, SolveType.SELF, member, problem5, now));

        //when
        SolveTimeTrendsResponse response = solvedStatisticsReader.getSolveTimeTrends(
                "testUser",
                SolvedPeriod.WEEK,
                TierGroup.NONE,
                null,
                now
        );

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.period()).isEqualTo("1주");
            softly.assertThat(response.tierGroup()).isEqualTo("NONE");
            softly.assertThat(response.totalSolvedCount()).isEqualTo(5L);
            softly.assertThat(response.trends()).hasSize(2); // 1월 9일은 SOLUTION만 있으므로 제외

            // 1월 8일: SELF 2개 (3600초, 1800초)
            TrendPoint day1 = response.trends().get(0);
            softly.assertThat(day1.date()).isEqualTo("2024-01-08");
            softly.assertThat(day1.averageSeconds()).isEqualTo((3600.0 + 1800.0) / 2);
            softly.assertThat(day1.solvedCount()).isEqualTo(2L);

            // 1월 10일: SELF 2개 (2400초, 3000초)
            TrendPoint day2 = response.trends().get(1);
            softly.assertThat(day2.date()).isEqualTo("2024-01-10");
            softly.assertThat(day2.averageSeconds()).isEqualTo((2400.0 + 3000.0) / 2);
            softly.assertThat(day2.solvedCount()).isEqualTo(2L);
        });
    }

    @Test
    void 독립_풀이_비율_추이를_날짜별로_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));
        LocalDateTime now = LocalDateTime.of(2024, 1, 10, 12, 0);

        Problem problem1 = problemRepository.save(createProblem("문제1", 1000L, Tier.B1));
        Problem problem2 = problemRepository.save(createProblem("문제2", 1001L, Tier.B2));
        Problem problem3 = problemRepository.save(createProblem("문제3", 1002L, Tier.S3));
        Problem problem4 = problemRepository.save(createProblem("문제4", 1003L, Tier.S2));
        Problem problem5 = problemRepository.save(createProblem("문제5", 1004L, Tier.G1));
        Problem problem6 = problemRepository.save(createProblem("문제6", 1005L, Tier.G2));

        // 1월 8일: 3문제 (SELF 2개, SOLUTION 1개)
        solvedRepository.save(createSolved(3600, SolveType.SELF, member, problem1, now.minusDays(2)));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem2, now.minusDays(2)));
        solvedRepository.save(createSolved(5400, SolveType.SOLUTION, member, problem3, now.minusDays(2)));

        // 1월 9일: 1문제 (SELF 1개)
        solvedRepository.save(createSolved(2400, SolveType.SELF, member, problem4, now.minusDays(1)));

        // 1월 10일: 2문제 (SELF 1개, SOLUTION 1개)
        solvedRepository.save(createSolved(3000, SolveType.SELF, member, problem5, now));
        solvedRepository.save(createSolved(4200, SolveType.SOLUTION, member, problem6, now));

        //when
        IndependentSolveTrendsResponse response = solvedStatisticsReader.getIndependentSolveTrends(
                "testUser",
                SolvedPeriod.WEEK,
                TierGroup.NONE,
                null,
                now
        );

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.period()).isEqualTo("1주");
            softly.assertThat(response.tierGroup()).isEqualTo("NONE");
            softly.assertThat(response.totalIndependentCount()).isEqualTo(4L); // SELF 총 4개
            softly.assertThat(response.totalTotalCount()).isEqualTo(6L); // 전체 6개
            softly.assertThat(response.trends()).hasSize(3);

            // 1월 8일: SELF 2개 / 전체 3개
            IndependentRatioPoint day1 = response.trends().get(0);
            softly.assertThat(day1.date()).isEqualTo("2024-01-08");
            softly.assertThat(day1.independentCount()).isEqualTo(2L);
            softly.assertThat(day1.totalCount()).isEqualTo(3L);

            // 1월 9일: SELF 1개 / 전체 1개
            IndependentRatioPoint day2 = response.trends().get(1);
            softly.assertThat(day2.date()).isEqualTo("2024-01-09");
            softly.assertThat(day2.independentCount()).isEqualTo(1L);
            softly.assertThat(day2.totalCount()).isEqualTo(1L);

            // 1월 10일: SELF 1개 / 전체 2개
            IndependentRatioPoint day3 = response.trends().get(2);
            softly.assertThat(day3.date()).isEqualTo("2024-01-10");
            softly.assertThat(day3.independentCount()).isEqualTo(1L);
            softly.assertThat(day3.totalCount()).isEqualTo(2L);
        });
    }

    @Test
    void 티어_그룹별_평균_풀이_시간_추이를_날짜별로_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));
        LocalDateTime now = LocalDateTime.of(2024, 1, 10, 12, 0);

        Problem bronzeProblem1 = problemRepository.save(createProblem("브론즈1", 1000L, Tier.B1));
        Problem bronzeProblem2 = problemRepository.save(createProblem("브론즈2", 1001L, Tier.B2));
        Problem silverProblem = problemRepository.save(createProblem("실버", 1002L, Tier.S3));
        Problem bronzeProblem3 = problemRepository.save(createProblem("브론즈3", 1003L, Tier.B3));

        // 1월 8일: 브론즈 1개 (3600초)
        solvedRepository.save(createSolved(3600, SolveType.SELF, member, bronzeProblem1, now.minusDays(2)));

        // 1월 9일: 브론즈 1개 (1800초), 실버 1개
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, bronzeProblem2, now.minusDays(1)));
        solvedRepository.save(createSolved(5400, SolveType.SELF, member, silverProblem, now.minusDays(1)));

        // 1월 10일: 브론즈 1개 (2400초)
        solvedRepository.save(createSolved(2400, SolveType.SELF, member, bronzeProblem3, now));

        //when
        SolveTimeTrendsResponse response = solvedStatisticsReader.getSolveTimeTrends(
                "testUser",
                SolvedPeriod.WEEK,
                TierGroup.BRONZE,
                null,
                now
        );

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.period()).isEqualTo("1주");
            softly.assertThat(response.tierGroup()).isEqualTo("BRONZE");
            softly.assertThat(response.totalSolvedCount()).isEqualTo(3L);
            softly.assertThat(response.trends()).hasSize(3);

            // 1월 8일: 브론즈 1개
            TrendPoint day1 = response.trends().get(0);
            softly.assertThat(day1.date()).isEqualTo("2024-01-08");
            softly.assertThat(day1.averageSeconds()).isEqualTo(3600.0);
            softly.assertThat(day1.solvedCount()).isEqualTo(1L);

            // 1월 9일: 브론즈 1개
            TrendPoint day2 = response.trends().get(1);
            softly.assertThat(day2.date()).isEqualTo("2024-01-09");
            softly.assertThat(day2.averageSeconds()).isEqualTo(1800.0);
            softly.assertThat(day2.solvedCount()).isEqualTo(1L);

            // 1월 10일: 브론즈 1개
            TrendPoint day3 = response.trends().get(2);
            softly.assertThat(day3.date()).isEqualTo("2024-01-10");
            softly.assertThat(day3.averageSeconds()).isEqualTo(2400.0);
            softly.assertThat(day3.solvedCount()).isEqualTo(1L);
        });
    }

    @Test
    void 데이터가_없는_경우_빈_추이를_반환한다() {
        //given
        Member member = memberRepository.save(createMember(1L, "emptyUser"));
        LocalDateTime now = LocalDateTime.now();

        //when
        SolveTimeTrendsResponse solveTimeResponse = solvedStatisticsReader.getSolveTimeTrends(
                "emptyUser",
                SolvedPeriod.WEEK,
                TierGroup.NONE,
                null,
                now
        );

        IndependentSolveTrendsResponse independentResponse = solvedStatisticsReader.getIndependentSolveTrends(
                "emptyUser",
                SolvedPeriod.WEEK,
                TierGroup.NONE,
                null,
                now
        );

        //then
        assertSoftly(softly -> {
            softly.assertThat(solveTimeResponse.trends()).isEmpty();
            softly.assertThat(solveTimeResponse.totalSolvedCount()).isEqualTo(0L);

            softly.assertThat(independentResponse.trends()).isEmpty();
            softly.assertThat(independentResponse.totalIndependentCount()).isEqualTo(0L);
            softly.assertThat(independentResponse.totalTotalCount()).isEqualTo(0L);
        });
    }

    @Test
    void 태그별_평균_풀이_시간_추이를_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));
        LocalDateTime now = LocalDateTime.of(2024, 1, 10, 12, 0);

        // DP 태그와 그리디 태그 생성
        Tag dpTag = tagRepository.save(new Tag(1, "dp", "다이나믹 프로그래밍"));
        Tag greedyTag = tagRepository.save(new Tag(2, "greedy", "그리디 알고리즘"));

        // 문제 생성
        Problem dpProblem1 = problemRepository.save(createProblem("DP문제1", 1000L, Tier.G1));
        Problem dpProblem2 = problemRepository.save(createProblem("DP문제2", 1001L, Tier.G2));
        Problem greedyProblem = problemRepository.save(createProblem("그리디문제", 1002L, Tier.S1));
        Problem dpProblem3 = problemRepository.save(createProblem("DP문제3", 1003L, Tier.G3));

        // 문제-태그 연결
        problemTagRepository.save(new ProblemTag(dpProblem1, dpTag));
        problemTagRepository.save(new ProblemTag(dpProblem2, dpTag));
        problemTagRepository.save(new ProblemTag(greedyProblem, greedyTag));
        problemTagRepository.save(new ProblemTag(dpProblem3, dpTag));

        // 1월 8일: DP 2문제 풀이 (3600초, 1800초)
        solvedRepository.save(createSolved(3600, SolveType.SELF, member, dpProblem1, now.minusDays(2)));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, dpProblem2, now.minusDays(2)));

        // 1월 9일: 그리디 1문제, DP는 없음 (5400초)
        solvedRepository.save(createSolved(5400, SolveType.SOLUTION, member, greedyProblem, now.minusDays(1)));

        // 1월 10일: DP 1문제 풀이 (2400초)
        solvedRepository.save(createSolved(2400, SolveType.SELF, member, dpProblem3, now));

        //when
        SolveTimeTrendsResponse response = solvedStatisticsReader.getSolveTimeTrends(
                "testUser",
                SolvedPeriod.WEEK,
                TierGroup.NONE,
                TagKey.DP,
                now
        );

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.period()).isEqualTo("1주");
            softly.assertThat(response.tierGroup()).isEqualTo("NONE");
            softly.assertThat(response.totalSolvedCount()).isEqualTo(3L); // DP 문제만 3개
            softly.assertThat(response.trends()).hasSize(2); // 그리디 푼 날은 제외

            // 1월 8일: DP 2개
            TrendPoint day1 = response.trends().get(0);
            softly.assertThat(day1.date()).isEqualTo("2024-01-08");
            softly.assertThat(day1.averageSeconds()).isEqualTo((3600.0 + 1800.0) / 2);
            softly.assertThat(day1.solvedCount()).isEqualTo(2L);

            // 1월 10일: DP 1개 (1월 9일은 그리디만 풀어서 제외)
            TrendPoint day2 = response.trends().get(1);
            softly.assertThat(day2.date()).isEqualTo("2024-01-10");
            softly.assertThat(day2.averageSeconds()).isEqualTo(2400.0);
            softly.assertThat(day2.solvedCount()).isEqualTo(1L);
        });
    }

    @Test
    void 태그와_티어_그룹을_함께_필터링할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));
        LocalDateTime now = LocalDateTime.of(2024, 1, 10, 12, 0);

        // 그리디 태그 생성
        Tag greedyTag = tagRepository.save(new Tag(1, "greedy", "그리디 알고리즘"));

        // 문제 생성: 골드 그리디, 실버 그리디, 브론즈 그리디
        Problem goldGreedy = problemRepository.save(createProblem("골드그리디", 1000L, Tier.G1));
        Problem silverGreedy = problemRepository.save(createProblem("실버그리디", 1001L, Tier.S1));
        Problem goldGreedy2 = problemRepository.save(createProblem("골드그리디2", 1002L, Tier.G2));

        // 문제-태그 연결
        problemTagRepository.save(new ProblemTag(goldGreedy, greedyTag));
        problemTagRepository.save(new ProblemTag(silverGreedy, greedyTag));
        problemTagRepository.save(new ProblemTag(goldGreedy2, greedyTag));

        // 1월 8일: 골드 1개, 실버 1개
        solvedRepository.save(createSolved(3600, SolveType.SELF, member, goldGreedy, now.minusDays(2)));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, silverGreedy, now.minusDays(2)));

        // 1월 9일: 골드 1개
        solvedRepository.save(createSolved(2400, SolveType.SELF, member, goldGreedy2, now.minusDays(1)));

        //when - 그리디 태그 + 골드 티어로 필터링
        SolveTimeTrendsResponse response = solvedStatisticsReader.getSolveTimeTrends(
                "testUser",
                SolvedPeriod.WEEK,
                TierGroup.GOLD,
                TagKey.GREEDY,
                now
        );

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.period()).isEqualTo("1주");
            softly.assertThat(response.tierGroup()).isEqualTo("GOLD");
            softly.assertThat(response.totalSolvedCount()).isEqualTo(2L); // 골드 그리디만 2개
            softly.assertThat(response.trends()).hasSize(2);

            // 1월 8일: 골드 1개 (실버는 제외)
            TrendPoint day1 = response.trends().get(0);
            softly.assertThat(day1.date()).isEqualTo("2024-01-08");
            softly.assertThat(day1.averageSeconds()).isEqualTo(3600.0);
            softly.assertThat(day1.solvedCount()).isEqualTo(1L);

            // 1월 9일: 골드 1개
            TrendPoint day2 = response.trends().get(1);
            softly.assertThat(day2.date()).isEqualTo("2024-01-09");
            softly.assertThat(day2.averageSeconds()).isEqualTo(2400.0);
            softly.assertThat(day2.solvedCount()).isEqualTo(1L);
        });
    }

    @Test
    void 태그별_독립_풀이_비율_추이를_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));
        LocalDateTime now = LocalDateTime.of(2024, 1, 10, 12, 0);

        // 구현 태그 생성
        Tag implTag = tagRepository.save(new Tag(1, "implementation", "구현"));

        // 문제 생성
        Problem implProblem1 = problemRepository.save(createProblem("구현1", 1000L, Tier.B1));
        Problem implProblem2 = problemRepository.save(createProblem("구현2", 1001L, Tier.B2));
        Problem implProblem3 = problemRepository.save(createProblem("구현3", 1002L, Tier.S1));
        Problem implProblem4 = problemRepository.save(createProblem("구현4", 1003L, Tier.S2));

        // 문제-태그 연결
        problemTagRepository.save(new ProblemTag(implProblem1, implTag));
        problemTagRepository.save(new ProblemTag(implProblem2, implTag));
        problemTagRepository.save(new ProblemTag(implProblem3, implTag));
        problemTagRepository.save(new ProblemTag(implProblem4, implTag));

        // 1월 8일: 구현 2문제 (SELF 1개, SOLUTION 1개)
        solvedRepository.save(createSolved(3600, SolveType.SELF, member, implProblem1, now.minusDays(2)));
        solvedRepository.save(createSolved(1800, SolveType.SOLUTION, member, implProblem2, now.minusDays(2)));

        // 1월 9일: 구현 1문제 (SELF)
        solvedRepository.save(createSolved(2400, SolveType.SELF, member, implProblem3, now.minusDays(1)));

        // 1월 10일: 구현 1문제 (SOLUTION)
        solvedRepository.save(createSolved(3000, SolveType.SOLUTION, member, implProblem4, now));

        //when
        IndependentSolveTrendsResponse response = solvedStatisticsReader.getIndependentSolveTrends(
                "testUser",
                SolvedPeriod.WEEK,
                TierGroup.NONE,
                TagKey.IMPLEMENTATION,
                now
        );

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.period()).isEqualTo("1주");
            softly.assertThat(response.tierGroup()).isEqualTo("NONE");
            softly.assertThat(response.totalIndependentCount()).isEqualTo(2L); // SELF 총 2개
            softly.assertThat(response.totalTotalCount()).isEqualTo(4L); // 전체 4개
            softly.assertThat(response.trends()).hasSize(3);

            // 1월 8일: SELF 1개 / 전체 2개
            IndependentRatioPoint day1 = response.trends().get(0);
            softly.assertThat(day1.date()).isEqualTo("2024-01-08");
            softly.assertThat(day1.independentCount()).isEqualTo(1L);
            softly.assertThat(day1.totalCount()).isEqualTo(2L);

            // 1월 9일: SELF 1개 / 전체 1개
            IndependentRatioPoint day2 = response.trends().get(1);
            softly.assertThat(day2.date()).isEqualTo("2024-01-09");
            softly.assertThat(day2.independentCount()).isEqualTo(1L);
            softly.assertThat(day2.totalCount()).isEqualTo(1L);

            // 1월 10일: SELF 0개 / 전체 1개
            IndependentRatioPoint day3 = response.trends().get(2);
            softly.assertThat(day3.date()).isEqualTo("2024-01-10");
            softly.assertThat(day3.independentCount()).isEqualTo(0L);
            softly.assertThat(day3.totalCount()).isEqualTo(1L);
        });
    }
}
