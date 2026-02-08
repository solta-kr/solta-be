package kr.solta.domain;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TierAveragesTest {

    @Test
    void TierGroup별로_TierAverage를_그룹화할_수_있다() {
        //given
        List<TierAverage> tierAverages = List.of(
                new TierAverage(Tier.B1, 3600.0, 5, 4),
                new TierAverage(Tier.B3, 1800.0, 3, 2),
                new TierAverage(Tier.S2, 5400.0, 7, 5),
                new TierAverage(Tier.G1, 7200.0, 2, 2),
                new TierAverage(Tier.G3, 4800.0, 4, 3)
        );
        TierAverages sut = new TierAverages(tierAverages);

        //when
        Map<TierGroup, List<TierAverage>> result = sut.toTierGroupAverageMap();

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.get(TierGroup.BRONZE)).hasSize(5)
                    .containsExactly(
                            new TierAverage(Tier.B5, null, 0, 0),
                            new TierAverage(Tier.B4, null, 0, 0),
                            new TierAverage(Tier.B3, 1800.0, 3, 2),
                            new TierAverage(Tier.B2, null, 0, 0),
                            new TierAverage(Tier.B1, 3600.0, 5, 4)
                    );

            softly.assertThat(result.get(TierGroup.SILVER)).hasSize(5)
                    .containsExactly(
                            new TierAverage(Tier.S5, null, 0, 0),
                            new TierAverage(Tier.S4, null, 0, 0),
                            new TierAverage(Tier.S3, null, 0, 0),
                            new TierAverage(Tier.S2, 5400.0, 7, 5),
                            new TierAverage(Tier.S1, null, 0, 0)
                    );

            softly.assertThat(result.get(TierGroup.GOLD)).hasSize(5)
                    .containsExactly(
                            new TierAverage(Tier.G5, null, 0, 0),
                            new TierAverage(Tier.G4, null, 0, 0),
                            new TierAverage(Tier.G3, 4800.0, 4, 3),
                            new TierAverage(Tier.G2, null, 0, 0),
                            new TierAverage(Tier.G1, 7200.0, 2, 2)
                    );
        });
    }
}
