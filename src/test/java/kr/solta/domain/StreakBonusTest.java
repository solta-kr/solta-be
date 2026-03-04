package kr.solta.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StreakBonusTest {

    @ParameterizedTest
    @CsvSource({
            "0,   0.00",    // 스트릭 없음
            "6,   0.00",    // NONE 상한
            "7,   0.10",    // WEEK 시작
            "13,  0.10",    // WEEK 상한
            "14,  0.20",    // BIWEEK 시작
            "29,  0.20",    // BIWEEK 상한
            "30,  0.30",    // MONTH 시작
            "100, 0.30"     // MONTH 이상도 동일
    })
    void 스트릭_일수에_따라_올바른_보너스_배율을_반환한다(final int streak, final String expectedMultiplier) {
        assertThat(StreakBonus.of(streak)).isEqualByComparingTo(new BigDecimal(expectedMultiplier));
    }
}
