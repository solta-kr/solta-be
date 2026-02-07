package kr.solta.application.provided;

import static kr.solta.support.TestFixtures.createMember;
import static kr.solta.support.TestFixtures.createSolved;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import java.util.Map;
import kr.solta.application.provided.request.SolvedSortType;
import kr.solta.application.provided.request.TagKey;
import kr.solta.application.provided.response.SolvedWithTags;
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
import kr.solta.domain.TierAverage;
import kr.solta.domain.TierGroup;
import kr.solta.domain.TierGroupAverage;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SolvedFinderTest extends IntegrationTest {

    @Autowired
    private SolvedFinder solvedFinder;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ProblemTagRepository problemTagRepository;

    @Autowired
    private SolvedRepository solvedRepository;

    @Test
    void 사용자가_푼_풀이를_문제_태그와_함께_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember());

        Problem problem1 = createProblem("문제1", 1000L, Tier.B1);
        Problem problem2 = createProblem("문제2", 1001L, Tier.S3);

        Tag tag1 = createTag(1, "implementation", "구현");
        Tag tag2 = createTag(2, "math", "수학");
        Tag tag3 = createTag(3, "dp", "다이나믹 프로그래밍");

        createProblemTag(problem1, tag1);
        createProblemTag(problem1, tag2);
        createProblemTag(problem2, tag2);
        createProblemTag(problem2, tag3);

        Solved solved1 = solvedRepository.save(
                Solved.register(3600, SolveType.SELF, member, problem1, java.time.LocalDateTime.now()));
        Solved solved2 = solvedRepository.save(
                Solved.register(1800, SolveType.SOLUTION, member, problem2, java.time.LocalDateTime.now()));

        //when
        List<SolvedWithTags> result = solvedFinder.findSolvedWithTags(member.getName());

        //then
        assertThat(result).hasSize(2)
                .containsExactly(
                        new SolvedWithTags(solved2, List.of(tag2, tag3)),
                        new SolvedWithTags(solved1, List.of(tag1, tag2))
                );
    }

    @Test
    void TierGroup별_평균_풀이_시간을_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));

        Problem bronzeProblem1 = problemRepository.save(createProblem("브론즈1", 1000L, Tier.B1));
        Problem bronzeProblem2 = problemRepository.save(createProblem("브론즈2", 1001L, Tier.B3));
        Problem silverProblem = problemRepository.save(createProblem("실버1", 1002L, Tier.S2));
        Problem goldProblem = problemRepository.save(createProblem("골드1", 1003L, Tier.G1));

        solvedRepository.save(createSolved(3600, SolveType.SELF, member, bronzeProblem1));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, bronzeProblem2));
        solvedRepository.save(createSolved(5400, SolveType.SELF, member, silverProblem));
        solvedRepository.save(createSolved(7200, SolveType.SELF, member, goldProblem));

        //when
        List<TierGroupAverage> result = solvedFinder.findTierGroupAverages(member.getName(), null);

        //then
        assertThat(result).hasSize(TierGroup.values().length)
                .extracting(TierGroupAverage::tierGroup, TierGroupAverage::averageSolvedSeconds,
                        TierGroupAverage::solvedCount)
                .containsExactly(
                        tuple(TierGroup.NONE, null, 0L),
                        tuple(TierGroup.UNRATED, null, 0L),
                        tuple(TierGroup.BRONZE, 2700.0, 2L),
                        tuple(TierGroup.SILVER, 5400.0, 1L),
                        tuple(TierGroup.GOLD, 7200.0, 1L),
                        tuple(TierGroup.PLATINUM, null, 0L),
                        tuple(TierGroup.DIAMOND, null, 0L),
                        tuple(TierGroup.RUBY, null, 0L)
                );
    }

    @Test
    void TierGroup별_평균_풀이_시간_조회시_존재하지_않는_사용자면_예외가_발생한다() {
        //given
        String notExistMemberName = "notExistUser";

        //when & then
        assertThatThrownBy(() -> solvedFinder.findTierGroupAverages(notExistMemberName, null))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("존재하지 않는 사용자입니다");
    }

    @Test
    void Tier별_평균_풀이_시간을_TierGroup으로_그룹화하여_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));

        Problem bronzeProblem1 = problemRepository.save(createProblem("브론즈1", 1000L, Tier.B1));
        Problem bronzeProblem3 = problemRepository.save(createProblem("브론즈3", 1001L, Tier.B3));
        Problem silverProblem2 = problemRepository.save(createProblem("실버2", 1002L, Tier.S2));
        Problem goldProblem1 = problemRepository.save(createProblem("골드1", 1003L, Tier.G1));
        Problem goldProblem3 = problemRepository.save(createProblem("골드3", 1004L, Tier.G3));

        solvedRepository.save(createSolved(3600, SolveType.SELF, member, bronzeProblem1));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, bronzeProblem3));
        solvedRepository.save(createSolved(5400, SolveType.SELF, member, silverProblem2));
        solvedRepository.save(createSolved(7200, SolveType.SELF, member, goldProblem1));
        solvedRepository.save(createSolved(4800, SolveType.SELF, member, goldProblem3));

        //when
        Map<TierGroup, List<TierAverage>> result = solvedFinder.findTierAverages(member.getName(), null);

        //then
        assertSoftly(softly -> {
            softly.assertThat(result).hasSize(TierGroup.values().length);
            softly.assertThat(result.get(TierGroup.BRONZE)).hasSize(5)
                    .extracting(TierAverage::tier, TierAverage::averageSolvedSeconds, TierAverage::solvedCount)
                    .containsExactly(
                            tuple(Tier.B5, null, 0L),
                            tuple(Tier.B4, null, 0L),
                            tuple(Tier.B3, 1800.0, 1L),
                            tuple(Tier.B2, null, 0L),
                            tuple(Tier.B1, 3600.0, 1L)
                    );
            softly.assertThat(result.get(TierGroup.GOLD)).hasSize(5)
                    .extracting(TierAverage::tier, TierAverage::averageSolvedSeconds, TierAverage::solvedCount)
                    .containsExactly(
                            tuple(Tier.G5, null, 0L),
                            tuple(Tier.G4, null, 0L),
                            tuple(Tier.G3, 4800.0, 1L),
                            tuple(Tier.G2, null, 0L),
                            tuple(Tier.G1, 7200.0, 1L)
                    );
        });
    }

    @Test
    void Tier별_평균_풀이_시간_조회시_존재하지_않는_사용자면_예외가_발생한다() {
        //given
        String notExistMemberName = "notExistUser";

        //when & then
        assertThatThrownBy(() -> solvedFinder.findTierAverages(notExistMemberName, null))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("존재하지 않는 사용자입니다");
    }

    @Test
    void 정답을_본_문제를_최신순으로_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));

        Problem problem1 = createProblem("문제1", 1000L, Tier.B1);
        Problem problem2 = createProblem("문제2", 1001L, Tier.S3);
        Problem problem3 = createProblem("문제3", 1002L, Tier.G2);

        Tag tag1 = createTag(1, "implementation", "구현");
        Tag tag2 = createTag(2, "math", "수학");

        createProblemTag(problem1, tag1);
        createProblemTag(problem2, tag2);

        Solved solved1 = solvedRepository.save(
                Solved.register(3600, SolveType.SOLUTION, member, problem1, java.time.LocalDateTime.now().minusDays(2)));
        Solved solved2 = solvedRepository.save(
                Solved.register(1800, SolveType.SOLUTION, member, problem2, java.time.LocalDateTime.now().minusDays(1)));
        solvedRepository.save(
                Solved.register(5400, SolveType.SELF, member, problem3, java.time.LocalDateTime.now()));

        //when
        List<SolvedWithTags> result = solvedFinder.findProblemsToRetry(member.getName(), SolvedSortType.LATEST);

        //then
        assertThat(result).hasSize(2)
                .extracting(solvedWithTags -> solvedWithTags.solved().getId())
                .containsExactly(solved2.getId(), solved1.getId());
    }

    @Test
    void 정답을_본_문제를_난이도순으로_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));

        Problem bronzeProblem = createProblem("브론즈", 1000L, Tier.B1);
        Problem silverProblem = createProblem("실버", 1001L, Tier.S3);
        Problem goldProblem = createProblem("골드", 1002L, Tier.G2);

        solvedRepository.save(
                Solved.register(3600, SolveType.SOLUTION, member, bronzeProblem, java.time.LocalDateTime.now()));
        solvedRepository.save(
                Solved.register(1800, SolveType.SOLUTION, member, silverProblem, java.time.LocalDateTime.now()));
        solvedRepository.save(
                Solved.register(5400, SolveType.SOLUTION, member, goldProblem, java.time.LocalDateTime.now()));

        //when
        List<SolvedWithTags> result = solvedFinder.findProblemsToRetry(member.getName(), SolvedSortType.TIER);

        //then
        assertThat(result).hasSize(3)
                .extracting(solvedWithTags -> solvedWithTags.solved().getProblem().getTier())
                .containsExactly(Tier.G2, Tier.S3, Tier.B1);
    }

    @Test
    void 정답을_본_문제를_풀이시간순으로_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));

        Problem problem1 = createProblem("문제1", 1000L, Tier.B1);
        Problem problem2 = createProblem("문제2", 1001L, Tier.S3);
        Problem problem3 = createProblem("문제3", 1002L, Tier.G2);

        solvedRepository.save(
                Solved.register(1800, SolveType.SOLUTION, member, problem1, java.time.LocalDateTime.now()));
        solvedRepository.save(
                Solved.register(5400, SolveType.SOLUTION, member, problem2, java.time.LocalDateTime.now()));
        solvedRepository.save(
                Solved.register(3600, SolveType.SOLUTION, member, problem3, java.time.LocalDateTime.now()));

        //when
        List<SolvedWithTags> result = solvedFinder.findProblemsToRetry(member.getName(), SolvedSortType.SOLVE_TIME);

        //then
        assertThat(result).hasSize(3)
                .extracting(solvedWithTags -> solvedWithTags.solved().getSolveTimeSeconds())
                .containsExactly(5400, 3600, 1800);
    }

    @Test
    void 정답을_본_문제_조회시_SELF로_푼_문제는_포함되지_않는다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));

        Problem solutionProblem = createProblem("정답본문제", 1000L, Tier.B1);
        Problem selfProblem1 = createProblem("스스로푼문제1", 1001L, Tier.S3);
        Problem selfProblem2 = createProblem("스스로푼문제2", 1002L, Tier.G2);

        Solved solved1 = solvedRepository.save(
                Solved.register(3600, SolveType.SOLUTION, member, solutionProblem, java.time.LocalDateTime.now()));
        solvedRepository.save(
                Solved.register(1800, SolveType.SELF, member, selfProblem1, java.time.LocalDateTime.now()));
        solvedRepository.save(
                Solved.register(5400, SolveType.SELF, member, selfProblem2, java.time.LocalDateTime.now()));

        //when
        List<SolvedWithTags> result = solvedFinder.findProblemsToRetry(member.getName(), SolvedSortType.LATEST);

        //then
        assertThat(result).hasSize(1)
                .extracting(solvedWithTags -> solvedWithTags.solved().getId())
                .containsExactly(solved1.getId());
    }

    @Test
    void 정답을_본_문제_조회시_존재하지_않는_사용자면_예외가_발생한다() {
        //given
        String notExistMemberName = "notExistUser";

        //when & then
        assertThatThrownBy(() -> solvedFinder.findProblemsToRetry(notExistMemberName, SolvedSortType.LATEST))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("존재하지 않는 사용자입니다");
    }

    @Test
    void 정답을_본_문제를_태그와_함께_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));

        Problem problem1 = createProblem("문제1", 1000L, Tier.B1);
        Problem problem2 = createProblem("문제2", 1001L, Tier.S3);

        Tag tag1 = createTag(1, "implementation", "구현");
        Tag tag2 = createTag(2, "math", "수학");
        Tag tag3 = createTag(3, "dp", "다이나믹 프로그래밍");

        createProblemTag(problem1, tag1);
        createProblemTag(problem1, tag2);
        createProblemTag(problem2, tag2);
        createProblemTag(problem2, tag3);

        Solved solved1 = solvedRepository.save(
                Solved.register(3600, SolveType.SOLUTION, member, problem1, java.time.LocalDateTime.now()));
        Solved solved2 = solvedRepository.save(
                Solved.register(1800, SolveType.SOLUTION, member, problem2, java.time.LocalDateTime.now()));

        //when
        List<SolvedWithTags> result = solvedFinder.findProblemsToRetry(member.getName(), SolvedSortType.LATEST);

        //then
        assertThat(result).hasSize(2)
                .containsExactly(
                        new SolvedWithTags(solved2, List.of(tag2, tag3)),
                        new SolvedWithTags(solved1, List.of(tag1, tag2))
                );
    }

    @Test
    void 태그별_Tier_평균_풀이_시간을_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));

        Tag dpTag = createTag(1, "dp", "다이나믹 프로그래밍");
        Tag greedyTag = createTag(2, "greedy", "그리디 알고리즘");

        Problem dpBronze = createProblem("DP브론즈", 1000L, Tier.B1);
        Problem dpGold = createProblem("DP골드", 1001L, Tier.G3);
        Problem greedySilver = createProblem("그리디실버", 1002L, Tier.S2);

        createProblemTag(dpBronze, dpTag);
        createProblemTag(dpGold, dpTag);
        createProblemTag(greedySilver, greedyTag);

        solvedRepository.save(createSolved(3600, SolveType.SELF, member, dpBronze));
        solvedRepository.save(createSolved(7200, SolveType.SELF, member, dpGold));
        solvedRepository.save(createSolved(2000, SolveType.SELF, member, greedySilver));

        //when
        Map<TierGroup, List<TierAverage>> result = solvedFinder.findTierAverages(member.getName(), TagKey.DP);

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.get(TierGroup.BRONZE)).hasSize(5)
                    .extracting(TierAverage::tier, TierAverage::averageSolvedSeconds, TierAverage::solvedCount)
                    .contains(tuple(Tier.B1, 3600.0, 1L));
            softly.assertThat(result.get(TierGroup.GOLD)).hasSize(5)
                    .extracting(TierAverage::tier, TierAverage::averageSolvedSeconds, TierAverage::solvedCount)
                    .contains(tuple(Tier.G3, 7200.0, 1L));
            // 그리디 문제는 DP 태그 필터에 포함되지 않아야 함
            softly.assertThat(result.get(TierGroup.SILVER)).hasSize(5)
                    .extracting(TierAverage::averageSolvedSeconds)
                    .containsOnly((Double) null);
        });
    }

    @Test
    void 태그별_TierGroup_평균_풀이_시간을_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));

        // DP 태그와 그리디 태그 생성
        Tag dpTag = createTag(1, "dp", "다이나믹 프로그래밍");
        Tag greedyTag = createTag(2, "greedy", "그리디 알고리즘");

        // DP 문제들 (다양한 티어)
        Problem dpBronze = createProblem("DP브론즈", 1000L, Tier.B1);
        Problem dpSilver = createProblem("DP실버", 1001L, Tier.S1);
        Problem dpGold1 = createProblem("DP골드1", 1002L, Tier.G1);
        Problem dpGold2 = createProblem("DP골드2", 1003L, Tier.G2);

        // 그리디 문제들
        Problem greedyBronze = createProblem("그리디브론즈", 1004L, Tier.B2);
        Problem greedySilver = createProblem("그리디실버", 1005L, Tier.S2);

        // 문제-태그 연결
        createProblemTag(dpBronze, dpTag);
        createProblemTag(dpSilver, dpTag);
        createProblemTag(dpGold1, dpTag);
        createProblemTag(dpGold2, dpTag);
        createProblemTag(greedyBronze, greedyTag);
        createProblemTag(greedySilver, greedyTag);

        // DP 문제 풀이 기록
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, dpBronze));  // 브론즈
        solvedRepository.save(createSolved(3600, SolveType.SOLUTION, member, dpSilver));  // 실버
        solvedRepository.save(createSolved(5400, SolveType.SELF, member, dpGold1));  // 골드
        solvedRepository.save(createSolved(7200, SolveType.SELF, member, dpGold2));  // 골드

        // 그리디 문제 풀이 기록 (DP 태그로 검색할 때는 제외되어야 함)
        solvedRepository.save(createSolved(2000, SolveType.SELF, member, greedyBronze));
        solvedRepository.save(createSolved(4000, SolveType.SELF, member, greedySilver));

        //when - DP 태그로 필터링
        List<TierGroupAverage> result = solvedFinder.findTierGroupAverages(member.getName(), TagKey.DP);

        //then
        assertThat(result).hasSize(TierGroup.values().length);

        // DP 태그를 가진 문제만 집계되어야 함
        TierGroupAverage bronze = result.stream()
                .filter(avg -> avg.tierGroup() == TierGroup.BRONZE)
                .findFirst()
                .orElseThrow();
        assertThat(bronze.averageSolvedSeconds()).isEqualTo(1800.0);
        assertThat(bronze.solvedCount()).isEqualTo(1L);
        assertThat(bronze.independentSolvedCount()).isEqualTo(1L);  // SELF

        TierGroupAverage silver = result.stream()
                .filter(avg -> avg.tierGroup() == TierGroup.SILVER)
                .findFirst()
                .orElseThrow();
        assertThat(silver.averageSolvedSeconds()).isEqualTo(3600.0);
        assertThat(silver.solvedCount()).isEqualTo(1L);
        assertThat(silver.independentSolvedCount()).isEqualTo(0L);  // SOLUTION

        TierGroupAverage gold = result.stream()
                .filter(avg -> avg.tierGroup() == TierGroup.GOLD)
                .findFirst()
                .orElseThrow();
        assertThat(gold.averageSolvedSeconds()).isEqualTo((5400.0 + 7200.0) / 2);
        assertThat(gold.solvedCount()).isEqualTo(2L);
        assertThat(gold.independentSolvedCount()).isEqualTo(2L);  // 둘 다 SELF

        // 나머지 티어는 0이어야 함
        TierGroupAverage platinum = result.stream()
                .filter(avg -> avg.tierGroup() == TierGroup.PLATINUM)
                .findFirst()
                .orElseThrow();
        assertThat(platinum.solvedCount()).isEqualTo(0L);
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
