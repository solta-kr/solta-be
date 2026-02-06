package kr.solta.application;

import java.time.LocalDateTime;
import java.util.List;
import kr.solta.application.provided.SolvedStatisticsReader;
import kr.solta.application.provided.response.IndependentRatioPoint;
import kr.solta.application.provided.response.IndependentSolveTrendsResponse;
import kr.solta.application.provided.response.SolveTimeTrendsResponse;
import kr.solta.application.provided.response.TrendPoint;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.application.required.dto.IndependentRatioData;
import kr.solta.application.required.dto.TrendData;
import kr.solta.domain.Member;
import kr.solta.domain.SolvedPeriod;
import kr.solta.domain.TagKey;
import kr.solta.domain.Tier;
import kr.solta.domain.TierGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SolvedStatisticsService implements SolvedStatisticsReader {

    private final MemberRepository memberRepository;
    private final SolvedRepository solvedRepository;

    @Transactional(readOnly = true)
    @Override
    public SolveTimeTrendsResponse getSolveTimeTrends(
            final String name,
            final SolvedPeriod solvedPeriod,
            final TierGroup tierGroup,
            final TagKey tagKey,
            final LocalDateTime now
    ) {
        Member member = getMemberByName(name);
        List<Tier> tiers = tierGroup.getTiers();

        List<TrendPoint> trends = getTrendPoints(member, solvedPeriod, tiers, tagKey, now);
        Long totalSolvedCount = getTotalSolvedCount(member.getId(), solvedPeriod.getStartDate(now), tiers, tagKey);

        return SolveTimeTrendsResponse.of(solvedPeriod, tierGroup, totalSolvedCount, trends);
    }

    @Transactional(readOnly = true)
    @Override
    public IndependentSolveTrendsResponse getIndependentSolveTrends(
            final String name,
            final SolvedPeriod solvedPeriod,
            final TierGroup tierGroup,
            final TagKey tagKey,
            final LocalDateTime now
    ) {
        Member member = getMemberByName(name);

        List<IndependentRatioPoint> trends = getIndependentRatioPoints(member, solvedPeriod, tierGroup.getTiers(), tagKey, now);

        return IndependentSolveTrendsResponse.of(
                solvedPeriod,
                tierGroup,
                trends
        );
    }

    private Member getMemberByName(final String name) {
        return memberRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. name: " + name));
    }

    private List<TrendPoint> getTrendPoints(
            final Member member,
            final SolvedPeriod solvedPeriod,
            final List<Tier> tiers,
            final TagKey tagKey,
            final LocalDateTime now
    ) {
        LocalDateTime startDate = solvedPeriod.getStartDate(now);
        String dateFormat = solvedPeriod.getAggregationType().getDateFormat();

        List<TrendData> trendData = getTrendData(member.getId(), startDate, dateFormat, tiers, tagKey);

        return trendData.stream()
                .map(data -> new TrendPoint(data.date(), data.averageSeconds(), data.solvedCount()))
                .toList();
    }

    private List<TrendData> getTrendData(
            final Long memberId,
            final LocalDateTime startDate,
            final String dateFormat,
            final List<Tier> tiers,
            final TagKey tagKey
    ) {
        if (tagKey == null) {
            return tiers.isEmpty()
                    ? solvedRepository.findSolveTimeTrendsAll(memberId, startDate, dateFormat)
                    : solvedRepository.findSolveTimeTrendsByTiers(memberId, startDate, dateFormat, tiers);
        }

        return tiers.isEmpty()
                ? solvedRepository.findSolveTimeTrendsByTag(memberId, startDate, dateFormat, tagKey.getKey())
                : solvedRepository.findSolveTimeTrendsByTiersAndTag(memberId, startDate, dateFormat, tiers, tagKey.getKey());
    }

    private Long getTotalSolvedCount(
            final Long memberId,
            final LocalDateTime startDate,
            final List<Tier> tiers,
            final TagKey tagKey
    ) {
        if (tagKey == null) {
            return tiers.isEmpty()
                    ? solvedRepository.countSolvedByPeriod(memberId, startDate)
                    : solvedRepository.countSolvedByPeriodAndTiers(memberId, startDate, tiers);
        }

        return tiers.isEmpty()
                ? solvedRepository.countSolvedByPeriodAndTag(memberId, startDate, tagKey.getKey())
                : solvedRepository.countSolvedByPeriodAndTiersAndTag(memberId, startDate, tiers, tagKey.getKey());
    }

    private List<IndependentRatioPoint> getIndependentRatioPoints(
            final Member member,
            final SolvedPeriod solvedPeriod,
            final List<Tier> tiers,
            final TagKey tagKey,
            final LocalDateTime now
    ) {
        LocalDateTime startDate = solvedPeriod.getStartDate(now);
        String dateFormat = solvedPeriod.getAggregationType().getDateFormat();

        List<IndependentRatioData> ratioData = getIndependentRatioData(member.getId(), startDate, dateFormat, tiers, tagKey);

        return ratioData.stream()
                .map(data -> new IndependentRatioPoint(data.date(), data.independentCount(), data.totalCount()))
                .toList();
    }

    private List<IndependentRatioData> getIndependentRatioData(
            final Long memberId,
            final LocalDateTime startDate,
            final String dateFormat,
            final List<Tier> tiers,
            final TagKey tagKey
    ) {
        if (tagKey == null) {
            return tiers.isEmpty()
                    ? solvedRepository.findIndependentRatioTrendsAll(memberId, startDate, dateFormat)
                    : solvedRepository.findIndependentRatioTrendsByTiers(memberId, startDate, dateFormat, tiers);
        }

        return tiers.isEmpty()
                ? solvedRepository.findIndependentRatioTrendsByTag(memberId, startDate, dateFormat, tagKey.getKey())
                : solvedRepository.findIndependentRatioTrendsByTiersAndTag(memberId, startDate, dateFormat, tiers, tagKey.getKey());
    }
}
