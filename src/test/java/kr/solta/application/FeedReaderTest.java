package kr.solta.application;

import static kr.solta.support.TestFixtures.createMember;
import static kr.solta.support.TestFixtures.createProblem;
import static kr.solta.support.TestFixtures.createSolved;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDateTime;
import kr.solta.application.provided.FeedReader;
import kr.solta.application.provided.response.FeedItemResponse;
import kr.solta.application.provided.response.FeedResponse;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.SolveType;
import kr.solta.domain.Tier;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FeedReaderTest extends IntegrationTest {

    @Autowired
    private FeedReader feedReader;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SolvedRepository solvedRepository;

    @Test
    void 최근_풀이_피드를_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "alice"));
        Problem problem = problemRepository.save(createProblem("두 수의 합", 1000L, Tier.S1));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem));

        //when
        FeedResponse response = feedReader.getRecentFeed();

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.recentFeeds()).hasSize(1);

            FeedItemResponse item = response.recentFeeds().get(0);
            softly.assertThat(item.memberName()).isEqualTo("alice");
            softly.assertThat(item.problemBojId()).isEqualTo(1000L);
            softly.assertThat(item.problemTitle()).isEqualTo("두 수의 합");
            softly.assertThat(item.problemTier()).isEqualTo("S1");
            softly.assertThat(item.solveType()).isEqualTo("SELF");
        });
    }

    @Test
    void SELF와_SOLUTION_풀이_모두_피드에_포함된다() {
        //given
        Member member = memberRepository.save(createMember(1L, "bob"));
        Problem problem1 = problemRepository.save(createProblem("문제A", 2000L, Tier.B3));
        Problem problem2 = problemRepository.save(createProblem("문제B", 2001L, Tier.G2));
        solvedRepository.save(createSolved(3600, SolveType.SELF, member, problem1));
        solvedRepository.save(createSolved(0, SolveType.SOLUTION, member, problem2));

        //when
        FeedResponse response = feedReader.getRecentFeed();

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.recentFeeds()).hasSize(2);

            assertThat(response.recentFeeds())
                    .extracting(FeedItemResponse::solveType)
                    .containsExactlyInAnyOrder("SELF", "SOLUTION");
        });
    }

    @Test
    void 최근_1주일_통계가_집계된다() {
        //given
        Member member1 = memberRepository.save(createMember(1L, "charlie"));
        Member member2 = memberRepository.save(createMember(2L, "diana"));
        Problem problem1 = problemRepository.save(createProblem("문제1", 3000L, Tier.S3));
        Problem problem2 = problemRepository.save(createProblem("문제2", 3001L, Tier.G5));
        Problem problem3 = problemRepository.save(createProblem("문제3", 3002L, Tier.B1));

        solvedRepository.save(createSolved(1200, SolveType.SELF, member1, problem1));
        solvedRepository.save(createSolved(2400, SolveType.SELF, member2, problem2));
        solvedRepository.save(createSolved(600, SolveType.SELF, member1, problem3,
                LocalDateTime.now().minusDays(10)));

        //when
        FeedResponse response = feedReader.getRecentFeed();

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.stats().periodLabel()).isEqualTo("최근 1주일");
            softly.assertThat(response.stats().activeUserCount()).isEqualTo(2L);
            softly.assertThat(response.stats().totalSolveCount()).isEqualTo(2L);
        });
    }

    @Test
    void 풀이가_없으면_빈_피드를_반환한다() {
        //when
        FeedResponse response = feedReader.getRecentFeed();

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.recentFeeds()).isEmpty();
            softly.assertThat(response.stats().activeUserCount()).isEqualTo(0L);
            softly.assertThat(response.stats().totalSolveCount()).isEqualTo(0L);
        });
    }

    @Test
    void 최대_10개_풀이만_반환된다() {
        //given
        Member member = memberRepository.save(createMember(1L, "eve"));
        for (int i = 0; i < 12; i++) {
            Problem problem = problemRepository.save(createProblem("문제" + i, 4000L + i, Tier.B1));
            solvedRepository.save(createSolved(600, SolveType.SELF, member, problem));
        }

        //when
        FeedResponse response = feedReader.getRecentFeed();

        //then
        assertThat(response.recentFeeds()).hasSize(10);
    }

    @Test
    void 피드는_최신순으로_정렬된다() {
        //given
        Member member = memberRepository.save(createMember(1L, "frank"));
        Problem problem1 = problemRepository.save(createProblem("오래된 문제", 5000L, Tier.S2));
        Problem problem2 = problemRepository.save(createProblem("최신 문제", 5001L, Tier.G1));

        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem1,
                LocalDateTime.now().minusHours(5)));
        solvedRepository.save(createSolved(900, SolveType.SELF, member, problem2,
                LocalDateTime.now().minusHours(1)));

        //when
        FeedResponse response = feedReader.getRecentFeed();

        //then
        assertThat(response.recentFeeds()).hasSize(2);
        assertThat(response.recentFeeds().get(0).problemBojId()).isEqualTo(5001L);
    }
}
