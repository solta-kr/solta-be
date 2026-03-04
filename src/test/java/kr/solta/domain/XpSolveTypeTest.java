package kr.solta.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class XpSolveTypeTest {

    // ─── 답지를 본 풀이 (SOLUTION, multiplier=15.0, usesTime=false) ───────────

    @ParameterizedTest
    @CsvSource({
            "S1, 27",   // round(1.8 * 15.0) = 27
            "G1, 53",   // round(3.5 * 15.0) = round(52.5) = 53
            "P1, 90",   // round(6.0 * 15.0) = 90
    })
    void 답지를_본_풀이는_티어_가중치만으로_XP를_계산한다(final Tier tier, final int expectedXp) {
        assertThat(XpSolveType.SOLUTION.calculateBaseXp(tier, null)).isEqualTo(expectedXp);
    }

    @Test
    void 답지를_본_풀이는_풀이시간이_있어도_무시한다() {
        int withTime = XpSolveType.SOLUTION.calculateBaseXp(Tier.S1, 3600);
        int withoutTime = XpSolveType.SOLUTION.calculateBaseXp(Tier.S1, null);

        assertThat(withTime).isEqualTo(withoutTime);
    }

    // ─── 스스로 푼 풀이 (SELF, multiplier=1.5, usesTime=true) ────────────────

    @Test
    void 스스로_푼_풀이에서_풀이시간이_없으면_시간을_무시한다() {
        // round(1.8 * 1.5) = round(2.7) = 3
        assertThat(XpSolveType.SELF.calculateBaseXp(Tier.S1, null)).isEqualTo(3);
    }

    @Test
    void 스스로_푼_풀이에서_풀이시간이_0이면_시간을_무시한다() {
        assertThat(XpSolveType.SELF.calculateBaseXp(Tier.S1, 0)).isEqualTo(3);
    }

    @Test
    void 스스로_푼_30분_풀이의_XP를_계산한다() {
        // effectiveMinutes=30, round(30 * 1.8 * 1.5) = 81
        assertThat(XpSolveType.SELF.calculateBaseXp(Tier.S1, 1800)).isEqualTo(81);
    }

    @Test
    void 풀이시간이_60분_이내면_실제_시간이_그대로_반영된다() {
        // effectiveMinutes=60, round(60 * 1.8 * 1.5) = 162
        assertThat(XpSolveType.SELF.calculateBaseXp(Tier.S1, 3600)).isEqualTo(162);
    }

    @Test
    void 풀이시간이_60분을_초과하면_초과분이_절반_효율로_계산된다() {
        // 120분 → effectiveMinutes = 60 + (120-60) * 0.5 = 90
        // round(90 * 1.8 * 1.5) = round(243.0) = 243
        assertThat(XpSolveType.SELF.calculateBaseXp(Tier.S1, 7200)).isEqualTo(243);
    }

    @Test
    void 풀이시간이_4시간을_초과하면_캡이_적용된다() {
        // 240분 cap → effectiveMinutes = 60 + (240-60) * 0.5 = 150
        // round(150 * 1.8 * 1.5) = round(405.0) = 405
        int atCap = XpSolveType.SELF.calculateBaseXp(Tier.S1, 14400);   // 정확히 240분
        int overCap = XpSolveType.SELF.calculateBaseXp(Tier.S1, 18000); // 300분 → 240분과 동일

        assertThat(atCap).isEqualTo(405);
        assertThat(overCap).isEqualTo(405);
    }

    // ─── 복습 스스로 푼 풀이 (REVIEW_SELF, multiplier=2.0, usesTime=true) ────

    @Test
    void 복습_스스로_푼_풀이는_일반보다_높은_XP를_받는다() {
        // round(30 * 1.8 * 2.0) = 108 vs SELF 81
        int reviewXp = XpSolveType.REVIEW_SELF.calculateBaseXp(Tier.S1, 1800);
        int normalXp = XpSolveType.SELF.calculateBaseXp(Tier.S1, 1800);

        assertThat(reviewXp).isEqualTo(108);
        assertThat(reviewXp).isGreaterThan(normalXp);
    }

    // ─── 복습 답지 풀이 (REVIEW_SOLUTION, multiplier=18.0, usesTime=false) ───

    @Test
    void 복습_답지_풀이는_일반_답지보다_높은_XP를_받는다() {
        // round(1.8 * 18.0) = round(32.4) = 32 vs SOLUTION 27
        int reviewXp = XpSolveType.REVIEW_SOLUTION.calculateBaseXp(Tier.S1, null);
        int normalXp = XpSolveType.SOLUTION.calculateBaseXp(Tier.S1, null);

        assertThat(reviewXp).isEqualTo(32);
        assertThat(reviewXp).isGreaterThan(normalXp);
    }

    // ─── from ────────────────────────────────────────────────────────────────

    @ParameterizedTest
    @CsvSource({
            "SELF,     false, SELF",
            "SOLUTION, false, SOLUTION",
            "SELF,     true,  REVIEW_SELF",
            "SOLUTION, true,  REVIEW_SOLUTION"
    })
    void SolveType과_복습_여부로_XpSolveType을_반환한다(
            final SolveType solveType,
            final boolean isReview,
            final XpSolveType expected
    ) {
        assertThat(XpSolveType.from(solveType, isReview)).isEqualTo(expected);
    }
}
