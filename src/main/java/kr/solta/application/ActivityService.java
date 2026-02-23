package kr.solta.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;
import kr.solta.application.provided.ActivityReader;
import kr.solta.application.provided.response.ActivityData;
import kr.solta.application.provided.response.ActivityHeatmapResponse;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.application.required.dto.DailyActivity;
import kr.solta.domain.ActivityCalendar;
import kr.solta.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityService implements ActivityReader {

    private final MemberRepository memberRepository;
    private final SolvedRepository solvedRepository;

    @Transactional(readOnly = true)
    @Override
    public ActivityHeatmapResponse getActivityHeatmap(final String name, final LocalDate startDate, final LocalDate endDate) {
        Member member = memberRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. name: " + name));

        List<DailyActivity> rawActivities = solvedRepository.findDailyActivityByMemberIdBetween(member.getId(), startDate, endDate);
        List<ActivityData> activities = new ArrayList<>();
        for (DailyActivity raw : rawActivities) {
            activities.add(new ActivityData(raw.date(), raw.count(), raw.totalSeconds(), raw.independentCount()));
        }

        List<String> rawDates = solvedRepository.findDistinctSolvedDatesByMemberId(member.getId());
        List<LocalDate> allDates = new ArrayList<>();
        for (final String raw : rawDates) {
            allDates.add(LocalDate.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE));
        }
        ActivityCalendar calendar = new ActivityCalendar(allDates);

        return new ActivityHeatmapResponse(activities, calendar.currentStreak(), calendar.longestStreak());
    }
}
