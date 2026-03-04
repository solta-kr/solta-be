package kr.solta.domain;

import static kr.solta.support.TestFixtures.createMember;
import static kr.solta.support.TestFixtures.createProblem;
import static kr.solta.support.TestFixtures.createSolved;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class XpHistoryTest {

    // ─── 스트릭 없음 ──────────────────────────────────────────────────────────

    @Test
    void 스트릭이_없으면_기본_XP만_획득한다() {
        //given
        Member member = createMember();
        Problem problem = createProblem("문제", 1000L, Tier.S1);
        Solved solved = createSolved(1800, SolveType.SELF, member, problem);

        //when
        XpHistory history = XpHistory.create(member, solved, XpSolveType.SELF, 0);

        //then
        // baseXp = round(30 * 1.8 * 1.5) = 81, streakBonus = 0 → earnedXp = 81
        assertThat(history.getXpAmount()).isEqualTo(81);
        assertThat(history.getSolveType()).isEqualTo(XpSolveType.SELF);
        assertThat(history.getTierWeight()).isEqualByComparingTo(BigDecimal.valueOf(1.8));
        assertThat(history.getStreakBonus()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ─── 스트릭 보너스 ────────────────────────────────────────────────────────

    @Test
    void 스트릭_7일이면_10퍼센트_보너스가_적용된다() {
        //given
        Member member = createMember();
        Solved solved = createSolved(1800, SolveType.SELF, member, createProblem("문제", 1000L, Tier.S1));

        //when
        XpHistory history = XpHistory.create(member, solved, XpSolveType.SELF, 7);

        //then
        // round(81 * 1.10) = round(89.1) = 89
        assertThat(history.getXpAmount()).isEqualTo(89);
        assertThat(history.getStreakBonus()).isEqualByComparingTo(new BigDecimal("0.10"));
    }

    @Test
    void 스트릭_14일이면_20퍼센트_보너스가_적용된다() {
        //given
        Member member = createMember();
        Solved solved = createSolved(1800, SolveType.SELF, member, createProblem("문제", 1000L, Tier.S1));

        //when
        XpHistory history = XpHistory.create(member, solved, XpSolveType.SELF, 14);

        //then
        // round(81 * 1.20) = round(97.2) = 97
        assertThat(history.getXpAmount()).isEqualTo(97);
        assertThat(history.getStreakBonus()).isEqualByComparingTo(new BigDecimal("0.20"));
    }

    @Test
    void 스트릭_30일이면_30퍼센트_보너스가_적용된다() {
        //given
        Member member = createMember();
        Solved solved = createSolved(1800, SolveType.SELF, member, createProblem("문제", 1000L, Tier.S1));

        //when
        XpHistory history = XpHistory.create(member, solved, XpSolveType.SELF, 30);

        //then
        // round(81 * 1.30) = round(105.3) = 105
        assertThat(history.getXpAmount()).isEqualTo(105);
        assertThat(history.getStreakBonus()).isEqualByComparingTo(new BigDecimal("0.30"));
    }

    // ─── 답지 풀이 ────────────────────────────────────────────────────────────

    @Test
    void 답지를_본_풀이는_풀이시간_없이_XP를_계산한다() {
        //given
        Member member = createMember();
        Solved solved = createSolved(3600, SolveType.SOLUTION, member, createProblem("문제", 1000L, Tier.G1));

        //when
        XpHistory history = XpHistory.create(member, solved, XpSolveType.SOLUTION, 0);

        //then
        // round(3.5 * 15.0) = round(52.5) = 53
        assertThat(history.getXpAmount()).isEqualTo(53);
        assertThat(history.getSolveType()).isEqualTo(XpSolveType.SOLUTION);
    }

    // ─── 연관 관계 ────────────────────────────────────────────────────────────

    @Test
    void 생성된_히스토리에_멤버와_풀이가_올바르게_연결된다() {
        //given
        Member member = createMember();
        Problem problem = createProblem("문제", 1000L, Tier.S1);
        Solved solved = createSolved(1800, SolveType.SELF, member, problem);

        //when
        XpHistory history = XpHistory.create(member, solved, XpSolveType.SELF, 0);

        //then
        assertThat(history.getMember()).isEqualTo(member);
        assertThat(history.getSolved()).isEqualTo(solved);
    }
}
