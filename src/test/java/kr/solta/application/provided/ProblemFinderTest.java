package kr.solta.application.provided;

import static kr.solta.support.TestFixtures.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import kr.solta.application.provided.response.ProblemDetail;
import kr.solta.application.provided.response.ProblemPage;
import kr.solta.application.provided.response.ProblemWithTags;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.ProblemTagRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.application.required.TagRepository;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.ProblemTag;
import kr.solta.domain.SolveType;
import kr.solta.domain.Solved;
import kr.solta.domain.Tag;
import kr.solta.domain.Tier;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProblemFinderTest extends IntegrationTest {

    @Autowired
    private ProblemFinder problemFinder;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ProblemTagRepository problemTagRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SolvedRepository solvedRepository;

    @Test
    void 검색어_없이_전체_문제를_조회할_수_있다() {
        //given
        Problem problem1 = createProblem("피보나치 함수", 1003L, Tier.S3);
        Problem problem2 = createProblem("RGB거리", 1149L, Tier.S1);
        Problem problem3 = createProblem("계단 오르기", 2579L, Tier.S3);

        //when
        ProblemPage result = problemFinder.searchProblems(null, null);

        //then
        assertThat(result.problems()).hasSize(3)
                .extracting(pwt -> pwt.problem().getBojProblemId())
                .containsExactly(1003L, 1149L, 2579L);
    }

    @Test
    void bojProblemId_접두사로_문제를_검색할_수_있다() {
        //given
        createProblem("피보나치 함수", 1003L, Tier.S3);
        createProblem("RGB거리", 1149L, Tier.S1);
        createProblem("계단 오르기", 2579L, Tier.S3);

        //when
        ProblemPage result = problemFinder.searchProblems("10", null);

        //then
        assertThat(result.problems()).hasSize(1)
                .extracting(pwt -> pwt.problem().getBojProblemId())
                .containsExactly(1003L);
    }

    @Test
    void 검색_결과에_태그가_포함된다() {
        //given
        Problem problem = createProblem("피보나치 함수", 1003L, Tier.S3);
        Tag dpTag = createTag(1, "dp", "DP");
        Tag mathTag = createTag(2, "math", "수학");
        createProblemTag(problem, dpTag);
        createProblemTag(problem, mathTag);

        //when
        ProblemPage result = problemFinder.searchProblems(null, null);

        //then
        assertThat(result.problems()).hasSize(1);
        ProblemWithTags pwt = result.problems().getFirst();
        assertThat(pwt.tags()).extracting(Tag::getKorName)
                .containsExactlyInAnyOrder("DP", "수학");
    }

    @Test
    void 태그가_없는_문제는_빈_태그_목록을_반환한다() {
        //given
        createProblem("문제1", 1000L, Tier.B1);

        //when
        ProblemPage result = problemFinder.searchProblems(null, null);

        //then
        assertThat(result.problems()).hasSize(1);
        assertThat(result.problems().getFirst().tags()).isEmpty();
    }

    @Test
    void 커서_기반으로_다음_페이지를_조회할_수_있다() {
        //given
        for (int i = 1; i <= 15; i++) {
            createProblem("문제" + i, 1000L + i, Tier.S3);
        }

        //when
        ProblemPage firstPage = problemFinder.searchProblems(null, null);

        //then
        assertSoftly(softly -> {
            softly.assertThat(firstPage.problems()).hasSize(10);
            softly.assertThat(firstPage.hasNext()).isTrue();
            softly.assertThat(firstPage.problems().getFirst().problem().getBojProblemId()).isEqualTo(1001L);
            softly.assertThat(firstPage.problems().getLast().problem().getBojProblemId()).isEqualTo(1010L);
        });

        //when - 두번째 페이지
        Long lastBojProblemId = firstPage.problems().getLast().problem().getBojProblemId();
        ProblemPage secondPage = problemFinder.searchProblems(null, lastBojProblemId);

        //then
        assertSoftly(softly -> {
            softly.assertThat(secondPage.problems()).hasSize(5);
            softly.assertThat(secondPage.hasNext()).isFalse();
            softly.assertThat(secondPage.problems().getFirst().problem().getBojProblemId()).isEqualTo(1011L);
            softly.assertThat(secondPage.problems().getLast().problem().getBojProblemId()).isEqualTo(1015L);
        });
    }

    @Test
    void 검색어와_커서를_함께_사용할_수_있다() {
        //given
        for (int i = 0; i < 15; i++) {
            createProblem("문제" + i, 1000L + i, Tier.S3);
        }

        //when - "10"으로 검색하면 1000~1014 중 1000~1009 매칭 (10자리 접두사)
        ProblemPage firstPage = problemFinder.searchProblems("10", null);

        //then
        assertSoftly(softly -> {
            softly.assertThat(firstPage.problems()).hasSize(10);
            softly.assertThat(firstPage.hasNext()).isTrue();
        });

        //when - 다음 페이지
        Long lastBojProblemId = firstPage.problems().getLast().problem().getBojProblemId();
        ProblemPage secondPage = problemFinder.searchProblems("10", lastBojProblemId);

        //then
        assertSoftly(softly -> {
            softly.assertThat(secondPage.problems()).hasSize(5);
            softly.assertThat(secondPage.hasNext()).isFalse();
        });
    }

    @Test
    void 검색_결과가_없으면_빈_목록을_반환한다() {
        //given
        createProblem("문제1", 1000L, Tier.B1);

        //when
        ProblemPage result = problemFinder.searchProblems("9999", null);

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.problems()).isEmpty();
            softly.assertThat(result.hasNext()).isFalse();
        });
    }

    @Test
    void 정확히_10개일_때_hasNext는_false이다() {
        //given
        for (int i = 1; i <= 10; i++) {
            createProblem("문제" + i, 1000L + i, Tier.S3);
        }

        //when
        ProblemPage result = problemFinder.searchProblems(null, null);

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.problems()).hasSize(10);
            softly.assertThat(result.hasNext()).isFalse();
        });
    }

    @Test
    void 문제는_bojProblemId_오름차순으로_정렬된다() {
        //given
        createProblem("골드문제", 9095L, Tier.G1);
        createProblem("피보나치", 1003L, Tier.S3);
        createProblem("타일링", 11726L, Tier.S3);
        createProblem("계단", 2579L, Tier.S3);

        //when
        ProblemPage result = problemFinder.searchProblems(null, null);

        //then
        assertThat(result.problems())
                .extracting(pwt -> pwt.problem().getBojProblemId())
                .containsExactly(1003L, 2579L, 9095L, 11726L);
    }

    @Test
    void 백준_문제_번호로_문제_상세_정보를_조회할_수_있다() {
        //given
        Problem problem = createProblem("피보나치 함수", 1003L, Tier.S3);
        Tag dpTag = createTag(1, "dp", "DP");
        Tag mathTag = createTag(2, "math", "수학");
        createProblemTag(problem, dpTag);
        createProblemTag(problem, mathTag);

        //when
        ProblemDetail detail = problemFinder.findProblemDetail(1003L);

        //then
        assertSoftly(softly -> {
            softly.assertThat(detail.problem().getBojProblemId()).isEqualTo(1003L);
            softly.assertThat(detail.problem().getTitle()).isEqualTo("피보나치 함수");
            softly.assertThat(detail.problem().getTier()).isEqualTo(Tier.S3);
            softly.assertThat(detail.tags()).extracting(Tag::getKorName)
                    .containsExactlyInAnyOrder("DP", "수학");
        });
    }

    @Test
    void 문제_상세_조회시_풀이_통계를_함께_반환한다() {
        //given
        Problem problem = createProblem("피보나치 함수", 1003L, Tier.S3);
        Member member1 = memberRepository.save(createMember(1L, "user1"));
        Member member2 = memberRepository.save(createMember(2L, "user2"));
        Member member3 = memberRepository.save(createMember(3L, "user3"));

        solvedRepository.save(Solved.register(1200, SolveType.SELF, member1, problem, java.time.LocalDateTime.now()));
        solvedRepository.save(Solved.register(1800, SolveType.SELF, member2, problem, java.time.LocalDateTime.now()));
        solvedRepository.save(Solved.register(600, SolveType.SOLUTION, member3, problem, java.time.LocalDateTime.now()));

        //when
        ProblemDetail detail = problemFinder.findProblemDetail(1003L);

        //then
        assertSoftly(softly -> {
            softly.assertThat(detail.solvedStats().totalSolvedCount()).isEqualTo(3L);
            softly.assertThat(detail.solvedStats().independentSolvedCount()).isEqualTo(2L);
            softly.assertThat(detail.solvedStats().averageSolveTimeSeconds()).isEqualTo(1200.0);
            softly.assertThat(detail.solvedStats().shortestSolveTimeSeconds()).isEqualTo(600);
        });
    }

    @Test
    void 풀이_기록이_없는_문제의_통계는_0과_null을_반환한다() {
        //given
        createProblem("새 문제", 9999L, Tier.G1);

        //when
        ProblemDetail detail = problemFinder.findProblemDetail(9999L);

        //then
        assertSoftly(softly -> {
            softly.assertThat(detail.solvedStats().totalSolvedCount()).isEqualTo(0L);
            softly.assertThat(detail.solvedStats().independentSolvedCount()).isEqualTo(0L);
            softly.assertThat(detail.solvedStats().averageSolveTimeSeconds()).isNull();
            softly.assertThat(detail.solvedStats().shortestSolveTimeSeconds()).isNull();
        });
    }

    @Test
    void 존재하지_않는_백준_문제_번호로_조회시_예외가_발생한다() {
        //when & then
        assertThatThrownBy(() -> problemFinder.findProblemDetail(99999L))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("존재하지 않습니다");
    }

    private Problem createProblem(String title, long bojProblemId, Tier tier) {
        return problemRepository.save(new Problem(title, bojProblemId, tier));
    }

    private Tag createTag(int id, String key, String korName) {
        return tagRepository.save(new Tag(id, key, korName));
    }

    private ProblemTag createProblemTag(Problem problem, Tag tag) {
        return problemTagRepository.save(new ProblemTag(problem, tag));
    }
}
