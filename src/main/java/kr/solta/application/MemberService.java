package kr.solta.application;

import java.time.LocalDateTime;
import java.util.List;
import kr.solta.application.provided.MemberReader;
import kr.solta.application.provided.SolvedStatisticsReader;
import kr.solta.application.provided.response.IndependentRatioPoint;
import kr.solta.application.provided.response.IndependentSolveTrendsResponse;
import kr.solta.application.provided.response.MemberProfileResponse;
import kr.solta.application.provided.response.SolveTimeTrendsResponse;
import kr.solta.application.provided.response.TrendPoint;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.application.required.dto.AllSolvedAverage;
import kr.solta.application.required.dto.IndependentRatioData;
import kr.solta.application.required.dto.TrendData;
import kr.solta.domain.Member;
import kr.solta.domain.SolvedPeriod;
import kr.solta.domain.Tier;
import kr.solta.domain.TierGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService implements MemberReader, SolvedStatisticsReader {

    private final MemberRepository memberRepository;
    private final SolvedRepository solvedRepository;

    @Transactional(readOnly = true)
    @Override
    public Member getMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. memberId: " + memberId));
    }

    @Transactional(readOnly = true)
    @Override
    public MemberProfileResponse getMemberProfile(final String name) {
        Member member = getMemberByName(name);
        AllSolvedAverage allSolvedAverage = solvedRepository.findAllSolvedAverage(member);

        return MemberProfileResponse.of(member, allSolvedAverage);
    }

    @Transactional(readOnly = true)
    @Override
    public SolveTimeTrendsResponse getSolveTimeTrends(
            final String name,
            final SolvedPeriod solvedPeriod,
            final TierGroup tierGroup,
            final LocalDateTime now
    ) {
        Member member = getMemberByName(name);
        List<Tier> tiers = tierGroup.getTiers();

        List<TrendPoint> trends = getTrendPoints(member, solvedPeriod, tiers, now);
        Long totalSolvedCount = tiers.isEmpty()
                ? solvedRepository.countSolvedByPeriod(member.getId(), solvedPeriod.getStartDate(now))
                : solvedRepository.countSolvedByPeriodAndTiers(member.getId(), solvedPeriod.getStartDate(now), tiers);

        return SolveTimeTrendsResponse.of(solvedPeriod, tierGroup, totalSolvedCount, trends);
    }

    @Transactional(readOnly = true)
    @Override
    public IndependentSolveTrendsResponse getIndependentSolveTrends(
            final String name,
            final SolvedPeriod solvedPeriod,
            final TierGroup tierGroup,
            final LocalDateTime now
    ) {
        Member member = getMemberByName(name);

        List<IndependentRatioPoint> trends = getIndependentRatioPoints(member, solvedPeriod, tierGroup.getTiers(), now);

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
            final LocalDateTime now
    ) {
        LocalDateTime startDate = solvedPeriod.getStartDate(now);
        String dateFormat = solvedPeriod.getAggregationType().getDateFormat();

        List<TrendData> trendData = tiers.isEmpty()
                ? solvedRepository.findSolveTimeTrendsAll(member.getId(), startDate, dateFormat)
                : solvedRepository.findSolveTimeTrendsByTiers(member.getId(), startDate, dateFormat, tiers);

        return trendData.stream()
                .map(data -> new TrendPoint(data.date(), data.averageSeconds(), data.solvedCount()))
                .toList();
    }

    private List<IndependentRatioPoint> getIndependentRatioPoints(
            final Member member,
            final SolvedPeriod solvedPeriod,
            final List<Tier> tiers,
            final LocalDateTime now
    ) {
        LocalDateTime startDate = solvedPeriod.getStartDate(now);
        String dateFormat = solvedPeriod.getAggregationType().getDateFormat();

        List<IndependentRatioData> ratioData = tiers.isEmpty()
                ? solvedRepository.findIndependentRatioTrendsAll(member.getId(), startDate, dateFormat)
                : solvedRepository.findIndependentRatioTrendsByTiers(member.getId(), startDate, dateFormat, tiers);

        return ratioData.stream()
                .map(data -> new IndependentRatioPoint(data.date(), data.independentCount(), data.totalCount()))
                .toList();
    }
}
