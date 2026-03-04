package kr.solta.application.provided;

import static kr.solta.support.TestFixtures.createMember;
import static kr.solta.support.TestFixtures.createProblem;
import static kr.solta.support.TestFixtures.createSolved;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import kr.solta.application.provided.response.XpHistoryResponse;
import kr.solta.application.provided.response.XpSummaryResponse;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.Solved;
import kr.solta.domain.SolvedPeriod;
import kr.solta.domain.SolveType;
import kr.solta.domain.Tier;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class XpReaderTest extends IntegrationTest {

    @Autowired private XpReader xpReader;
    @Autowired private XpEarner xpEarner;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProblemRepository problemRepository;
    @Autowired private SolvedRepository solvedRepository;

    @Test
    void XP_요약을_조회하면_레벨_타이틀_진행률이_반환된다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem("문제", 1000L, Tier.S1));
        Solved solved = solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem));
        xpEarner.earnXp(member.getId(), solved.getId());
        // S1 SELF 30분, 스트릭 없음 → earnedXp = 81

        //when
        XpSummaryResponse response = xpReader.getXpSummary("testUser");

        //then
        assertThat(response.totalXp()).isEqualTo(81);
        assertThat(response.level()).isEqualTo(1);
        assertThat(response.title()).isEqualTo("코딩 새싹");
        assertThat(response.currentLevelXp()).isEqualTo(81);
        assertThat(response.nextLevelRequiredXp()).isEqualTo(150);
        assertThat(response.progressPercent()).isEqualTo(54); // 81 * 100 / 150 = 54
    }

    @Test
    void 주간_XP_히스토리를_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem("문제", 1000L, Tier.S1));
        Solved solved = solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem));
        xpEarner.earnXp(member.getId(), solved.getId());

        //when
        XpHistoryResponse response = xpReader.getXpHistory("testUser", SolvedPeriod.WEEK);

        //then
        assertThat(response.periodXp()).isEqualTo(81);
        assertThat(response.history()).hasSize(1);
        assertThat(response.history().get(0).xpAmount()).isEqualTo(81);
    }

    @Test
    void 전체_XP_히스토리를_조회하면_모든_내역이_합산된다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem1 = problemRepository.save(createProblem("문제1", 1000L, Tier.S1));
        Problem problem2 = problemRepository.save(createProblem("문제2", 1001L, Tier.G1));
        Solved solved1 = solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem1));
        Solved solved2 = solvedRepository.save(createSolved(3600, SolveType.SOLUTION, member, problem2));
        xpEarner.earnXp(member.getId(), solved1.getId()); // S1 SELF 30분 → 81
        xpEarner.earnXp(member.getId(), solved2.getId()); // G1 SOLUTION → 53

        //when
        XpHistoryResponse response = xpReader.getXpHistory("testUser", SolvedPeriod.ALL);

        //then
        assertThat(response.history()).hasSize(2);
        assertThat(response.periodXp()).isEqualTo(134); // 81 + 53
    }

    @Test
    void 존재하지_않는_사용자_XP_요약_조회시_예외가_발생한다() {
        assertThatThrownBy(() -> xpReader.getXpSummary("notExist"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("존재하지 않는 사용자입니다.");
    }

    @Test
    void 존재하지_않는_사용자_XP_히스토리_조회시_예외가_발생한다() {
        assertThatThrownBy(() -> xpReader.getXpHistory("notExist", SolvedPeriod.WEEK))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("존재하지 않는 사용자입니다.");
    }
}
