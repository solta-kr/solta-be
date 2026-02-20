package kr.solta.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.solta.application.BadgeSvgGenerator.TierBarData;
import kr.solta.application.provided.BadgeReader;
import kr.solta.application.provided.response.BadgeStatsResponse;
import kr.solta.application.provided.response.BadgeStatsResponse.TierDataItem;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.application.required.dto.BadgeSummaryStats;
import kr.solta.application.required.dto.TierGroupStat;
import kr.solta.domain.Member;
import kr.solta.domain.Tier;
import kr.solta.domain.TierGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BadgeService implements BadgeReader {

    private static final List<TierGroup> BADGE_TIER_GROUPS = List.of(
            TierGroup.BRONZE,
            TierGroup.SILVER,
            TierGroup.GOLD,
            TierGroup.PLATINUM,
            TierGroup.DIAMOND,
            TierGroup.RUBY
    );

    private static final Map<TierGroup, String> TIER_LABELS = Map.of(
            TierGroup.BRONZE, "B",
            TierGroup.SILVER, "S",
            TierGroup.GOLD, "G",
            TierGroup.PLATINUM, "P",
            TierGroup.DIAMOND, "D",
            TierGroup.RUBY, "R"
    );

    private static final Map<TierGroup, String> TIER_COLORS = Map.of(
            TierGroup.BRONZE, "hsl(30,70%,45%)",
            TierGroup.SILVER, "hsl(210,15%,60%)",
            TierGroup.GOLD, "hsl(45,100%,50%)",
            TierGroup.PLATINUM, "hsl(175,60%,55%)",
            TierGroup.DIAMOND, "hsl(200,100%,65%)",
            TierGroup.RUBY, "hsl(350,85%,55%)"
    );

    private final MemberRepository memberRepository;
    private final SolvedRepository solvedRepository;
    private final BadgeSvgGenerator badgeSvgGenerator;

    @Transactional(readOnly = true)
    @Override
    public String generateBadgeSvg(final String username) {
        BadgeStatsResponse stats = getBadgeStats(username);

        List<TierBarData> tierData = stats.tierData().stream()
                .map(item -> new TierBarData(item.label(), item.avgMinutes(), item.color()))
                .toList();

        return badgeSvgGenerator.generate(
                stats.username(),
                stats.totalMinutes(),
                stats.avgMinutes(),
                stats.selfSolveRate(),
                tierData
        );
    }

    @Transactional(readOnly = true)
    @Override
    public BadgeStatsResponse getBadgeStats(final String username) {
        Member member = memberRepository.findByName(username)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. username: " + username));

        BadgeSummaryStats summary = solvedRepository.findBadgeSummaryStats(member.getId());
        Integer selfSolveRateRaw = solvedRepository.findSelfSolveRate(member.getId());
        int selfSolveRate = selfSolveRateRaw != null ? selfSolveRateRaw : 0;

        List<Tier> allBadgeTiers = BADGE_TIER_GROUPS.stream()
                .flatMap(tg -> tg.getTiers().stream())
                .toList();
        List<TierGroupStat> tierGroupStats = solvedRepository.findBadgeTierGroupStats(member.getId(), allBadgeTiers);

        Map<TierGroup, Double> avgSecondsByGroup = mapToTierGroupAvg(tierGroupStats);

        int totalMinutes = (int) Math.round(summary.totalSeconds() / 60.0);
        int avgMinutes = (int) Math.round(summary.avgSeconds() / 60.0);

        List<TierDataItem> tierData = buildTierDataItems(avgSecondsByGroup);

        return new BadgeStatsResponse(username, totalMinutes, avgMinutes, selfSolveRate, tierData);
    }

    private Map<TierGroup, Double> mapToTierGroupAvg(List<TierGroupStat> stats) {
        Map<Tier, TierGroupStat> statByTier = stats.stream()
                .collect(Collectors.toMap(TierGroupStat::tier, s -> s));

        return BADGE_TIER_GROUPS.stream()
                .collect(Collectors.toMap(
                        tg -> tg,
                        tg -> {
                            List<TierGroupStat> matched = tg.getTiers().stream()
                                    .filter(statByTier::containsKey)
                                    .map(statByTier::get)
                                    .toList();
                            if (matched.isEmpty()) return 0.0;
                            double totalSeconds = matched.stream()
                                    .mapToDouble(s -> s.avgSeconds() * s.count())
                                    .sum();
                            long totalCount = matched.stream()
                                    .mapToLong(TierGroupStat::count)
                                    .sum();
                            return totalCount == 0 ? 0.0 : totalSeconds / totalCount;
                        }
                ));
    }

    private List<TierDataItem> buildTierDataItems(Map<TierGroup, Double> avgSecondsByGroup) {
        List<TierDataItem> result = new ArrayList<>();
        for (TierGroup tg : BADGE_TIER_GROUPS) {
            double avgSeconds = avgSecondsByGroup.getOrDefault(tg, 0.0);
            int avgMinutes = (int) Math.round(avgSeconds / 60.0);
            result.add(new TierDataItem(
                    TIER_LABELS.get(tg),
                    avgMinutes,
                    TIER_COLORS.get(tg)
            ));
        }
        return result;
    }
}
