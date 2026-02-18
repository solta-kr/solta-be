package kr.solta.application.provided;

import static kr.solta.support.TestFixtures.createMember;
import static kr.solta.support.TestFixtures.createProblem;
import static kr.solta.support.TestFixtures.createSolved;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import kr.solta.application.provided.response.SolveTimeDistributionResponse;
import kr.solta.application.provided.response.SolveTimeDistributionResponse.DistributionBucket;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.Tier;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SolveTimeDistributionTest extends IntegrationTest {

    @Autowired
    private SolvedStatisticsReader solvedStatisticsReader;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SolvedRepository solvedRepository;

    @Test
    void 여러_사용자가_문제를_풀었을_때_분포가_올바르게_생성된다() {
        //given
        Problem problem = problemRepository.save(createProblem("A+B", 1000L, Tier.B5));
        Member member1 = memberRepository.save(createMember(1L, "user1"));
        Member member2 = memberRepository.save(createMember(2L, "user2"));
        Member member3 = memberRepository.save(createMember(3L, "user3"));
        Member member4 = memberRepository.save(createMember(4L, "user4"));

        solvedRepository.save(createSolved(100, member1, problem));
        solvedRepository.save(createSolved(200, member2, problem));
        solvedRepository.save(createSolved(400, member3, problem));
        solvedRepository.save(createSolved(500, member4, problem));

        //when
        SolveTimeDistributionResponse response = solvedStatisticsReader.getSolveTimeDistribution(1000L, 250);

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.bojProblemId()).isEqualTo(1000L);
            softly.assertThat(response.title()).isEqualTo("A+B");
            softly.assertThat(response.tier()).isEqualTo(Tier.B5);
            softly.assertThat(response.totalSolverCount()).isEqualTo(4);
            softly.assertThat(response.bucketSize()).isEqualTo(300);
            softly.assertThat(response.distribution()).hasSize(2);

            DistributionBucket first = response.distribution().get(0);
            softly.assertThat(first.rangeStart()).isEqualTo(1);
            softly.assertThat(first.rangeEnd()).isEqualTo(300);
            softly.assertThat(first.count()).isEqualTo(2);

            DistributionBucket second = response.distribution().get(1);
            softly.assertThat(second.rangeStart()).isEqualTo(301);
            softly.assertThat(second.rangeEnd()).isEqualTo(600);
            softly.assertThat(second.count()).isEqualTo(2);
        });
    }

    @Test
    void 전달한_풀이_시간_기준_퍼센타일이_정확히_계산된다() {
        //given
        Problem problem = problemRepository.save(createProblem("A+B", 1000L, Tier.B5));

        for (int i = 1; i <= 10; i++) {
            Member member = memberRepository.save(createMember((long) i, "user" + i));
            solvedRepository.save(createSolved(i * 100, member, problem));
        }

        //when - 풀이 시간 500초: 5명이 같거나 빠름 → 상위 50%
        SolveTimeDistributionResponse response = solvedStatisticsReader.getSolveTimeDistribution(1000L, 500);

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.myPosition().solveTimeSeconds()).isEqualTo(500);
            softly.assertThat(response.myPosition().topPercent()).isEqualTo(50.0);
        });
    }

    @Test
    void 풀이_기록이_없을_때_빈_분포가_반환된다() {
        //given
        Problem problem = problemRepository.save(createProblem("빈 문제", 2000L, Tier.S1));

        //when
        SolveTimeDistributionResponse response = solvedStatisticsReader.getSolveTimeDistribution(2000L, 300);

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.bojProblemId()).isEqualTo(2000L);
            softly.assertThat(response.totalSolverCount()).isEqualTo(0);
            softly.assertThat(response.bucketSize()).isEqualTo(300);
            softly.assertThat(response.distribution()).isEmpty();
            softly.assertThat(response.myPosition().solveTimeSeconds()).isEqualTo(300);
            softly.assertThat(response.myPosition().topPercent()).isEqualTo(0.0);
        });
    }

    @Test
    void 존재하지_않는_문제_번호로_조회하면_예외가_발생한다() {
        //when & then
        assertThatThrownBy(() -> solvedStatisticsReader.getSolveTimeDistribution(9999L, 300))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("문제를 찾을 수 없습니다");
    }

    @Test
    void 가장_빠른_풀이_시간이면_상위_0퍼센트를_반환한다() {
        //given
        Problem problem = problemRepository.save(createProblem("쉬운 문제", 3000L, Tier.B5));

        for (int i = 1; i <= 5; i++) {
            Member member = memberRepository.save(createMember((long) i, "user" + i));
            solvedRepository.save(createSolved(i * 300, member, problem));
        }

        //when - 가장 빠른 시간(300)보다 빠른 100초 → 상위 0%
        SolveTimeDistributionResponse response = solvedStatisticsReader.getSolveTimeDistribution(3000L, 100);

        //then
        assertThat(response.myPosition().topPercent()).isEqualTo(0.0);
    }

    @Test
    void 빈_구간도_count_0으로_채워서_반환한다() {
        //given
        Problem problem = problemRepository.save(createProblem("갭 문제", 4000L, Tier.B5));
        Member member1 = memberRepository.save(createMember(1L, "user1"));
        Member member2 = memberRepository.save(createMember(2L, "user2"));

        solvedRepository.save(createSolved(100, member1, problem));   // bucket 0 (0~300)
        solvedRepository.save(createSolved(700, member2, problem));   // bucket 2 (600~900)

        //when
        SolveTimeDistributionResponse response = solvedStatisticsReader.getSolveTimeDistribution(4000L, 500);

        //then - bucket 1 (300~600)이 count 0으로 채워져야 함
        assertSoftly(softly -> {
            softly.assertThat(response.distribution()).hasSize(3);
            softly.assertThat(response.distribution().get(0).count()).isEqualTo(1);
            softly.assertThat(response.distribution().get(1).count()).isEqualTo(0);
            softly.assertThat(response.distribution().get(2).count()).isEqualTo(1);
        });
    }
}
