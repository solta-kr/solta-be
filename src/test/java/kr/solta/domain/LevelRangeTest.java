package kr.solta.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LevelRangeTest {

    // ─── calculateLevel ───────────────────────────────────────────────────────

    @ParameterizedTest
    @CsvSource({
            "0,      1",    // LV_1_10 시작
            "149,    1",    // 레벨 2 직전
            "150,    2",    // 레벨 2 시작
            "1499,   10",   // LV_11_30 직전
            "1500,   11",   // LV_11_30 시작
            "8999,   30",   // LV_31_60 직전
            "9000,   31",   // LV_31_60 시작
            "45000,  61",   // LV_61_90 시작
            "150000, 91",   // LV_91_95 시작
            "190000, 96",   // LV_96_100 시작
            "270000, 100",  // 최대
            "999999, 100"   // 초과 → 여전히 100
    })
    void XP에_따라_레벨이_올바르게_계산된다(final int xp, final int expectedLevel) {
        assertThat(LevelRange.calculateLevel(xp)).isEqualTo(expectedLevel);
    }

    // ─── getLevelThreshold ────────────────────────────────────────────────────

    @ParameterizedTest
    @CsvSource({
            "1,   0",
            "2,   150",
            "10,  1350",
            "11,  1500",
            "31,  9000",
            "100, 270000"
    })
    void 레벨별_임계_XP가_올바르다(final int level, final int expectedThreshold) {
        assertThat(LevelRange.getLevelThreshold(level)).isEqualTo(expectedThreshold);
    }

    // ─── getXpForNextLevel ────────────────────────────────────────────────────

    @Test
    void 레벨_1에서_다음_레벨까지_150XP가_필요하다() {
        assertThat(LevelRange.getXpForNextLevel(1)).isEqualTo(150);
    }

    @Test
    void 레벨_11에서_다음_레벨까지_375XP가_필요하다() {
        assertThat(LevelRange.getXpForNextLevel(11)).isEqualTo(375);
    }

    @Test
    void 레벨_100에서_다음_레벨_필요_XP는_0이다() {
        assertThat(LevelRange.getXpForNextLevel(100)).isEqualTo(0);
    }

    // ─── getProgressPercent ───────────────────────────────────────────────────

    @Test
    void XP_0_레벨_1에서_진행률은_0이다() {
        assertThat(LevelRange.getProgressPercent(0, 1)).isEqualTo(0);
    }

    @Test
    void 절반_XP에서_진행률은_50이다() {
        // 레벨 1: 150XP 필요. 75XP → 75 * 100 / 150 = 50%
        assertThat(LevelRange.getProgressPercent(75, 1)).isEqualTo(50);
    }

    @Test
    void 레벨_100에서_진행률은_100이다() {
        assertThat(LevelRange.getProgressPercent(270000, 100)).isEqualTo(100);
    }

    // ─── getTitle ─────────────────────────────────────────────────────────────

    @ParameterizedTest
    @CsvSource({
            "1,   Newbie",
            "10,  Newbie",
            "11,  Pupil",
            "31,  Specialist",
            "61,  Expert",
            "91,  Master",
            "100, Legendary"
    })
    void 레벨별_타이틀이_올바르다(final int level, final String expectedTitle) {
        assertThat(LevelRange.getTitle(level)).isEqualTo(expectedTitle);
    }
}
