package kr.solta.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TierAverages {

    private final List<TierAverage> tierAverages;

    public TierAverages(final List<TierAverage> tierAverages) {
        this.tierAverages = tierAverages;
    }

    public Map<TierGroup, List<TierAverage>> toTierGroupAverageMap() {
        Map<Tier, TierAverage> tierAverageMap = toTierAverageMap();

        return Arrays.stream(TierGroup.values())
                .collect(Collectors.toMap(
                        tg -> tg,
                        tg -> tg.getTiers().stream()
                                .map(t -> tierAverageMap.getOrDefault(t, TierAverage.none(t)))
                                .toList()
                ));
    }

    private Map<Tier, TierAverage> toTierAverageMap() {
        return this.tierAverages.stream()
                .collect(Collectors.toMap(
                        TierAverage::tier,
                        t -> t
                ));
    }
}
