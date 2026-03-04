package kr.solta.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import kr.solta.application.exception.NotFoundEntityException;
import kr.solta.application.provided.XpEarner;
import kr.solta.application.provided.XpReader;
import kr.solta.application.provided.response.XpHistoryResponse;
import kr.solta.application.provided.response.XpSummaryResponse;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ReviewScheduleRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.application.required.XpHistoryRepository;
import kr.solta.domain.ActivityCalendar;
import kr.solta.domain.Member;
import kr.solta.domain.ReviewStatus;
import kr.solta.domain.Solved;
import kr.solta.domain.SolvedPeriod;
import kr.solta.domain.Tier;
import kr.solta.domain.XpHistory;
import kr.solta.domain.XpSolveType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class XpService implements XpEarner, XpReader {

    private final XpHistoryRepository xpHistoryRepository;
    private final MemberRepository memberRepository;
    private final SolvedRepository solvedRepository;
    private final ReviewScheduleRepository reviewScheduleRepository;

    @Override
    public int earnXp(final Long memberId, final Long solvedId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundEntityException("존재하지 않는 사용자입니다."));
        Solved solved = solvedRepository.findById(solvedId)
                .orElseThrow(() -> new NotFoundEntityException("존재하지 않는 풀이입니다."));

        if (solved.getProblem().getTier() == Tier.UNRATED) return 0;

        boolean isReview = reviewScheduleRepository
                .findByMemberAndProblemAndStatus(member, solved.getProblem(), ReviewStatus.PENDING)
                .isPresent();
        XpSolveType xpSolveType = XpSolveType.from(solved.getSolveType(), isReview);
        int streak = calculateStreak(member);

        XpHistory history = XpHistory.create(member, solved, xpSolveType, streak);
        xpHistoryRepository.save(history);
        member.addXp(history.getXpAmount());
        return history.getXpAmount();
    }

    @Transactional(readOnly = true)
    @Override
    public XpSummaryResponse getXpSummary(final String username) {
        Member member = memberRepository.findByName(username)
                .orElseThrow(() -> new NotFoundEntityException("존재하지 않는 사용자입니다."));
        return XpSummaryResponse.from(member);
    }

    @Transactional(readOnly = true)
    @Override
    public XpHistoryResponse getXpHistory(final String username, final SolvedPeriod period) {
        Member member = memberRepository.findByName(username)
                .orElseThrow(() -> new NotFoundEntityException("존재하지 않는 사용자입니다."));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = period.getStartDate(now);
        List<XpHistory> histories = startDate == null
                ? xpHistoryRepository.findByMemberOrderByCreatedAtDesc(member)
                : xpHistoryRepository.findByMemberAndCreatedAtBetweenOrderByCreatedAtDesc(member, startDate, now);
        return XpHistoryResponse.of(histories);
    }

    private int calculateStreak(final Member member) {
        List<LocalDate> dates = solvedRepository
                .findDistinctSolvedDatesByMemberId(member.getId()).stream()
                .map(d -> LocalDate.parse(d, DateTimeFormatter.ISO_LOCAL_DATE))
                .toList();
        return new ActivityCalendar(dates).currentStreak();
    }
}
