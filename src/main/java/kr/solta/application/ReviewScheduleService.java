package kr.solta.application;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.solta.application.exception.NotFoundEntityException;
import kr.solta.application.provided.ReviewScheduleFinder;
import kr.solta.application.provided.ReviewScheduleUpdater;
import kr.solta.application.provided.request.AuthMember;
import kr.solta.application.provided.response.ReviewHistoryResponse;
import kr.solta.application.provided.response.ReviewListResponse;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemTagRepository;
import kr.solta.application.required.ReviewScheduleRepository;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.ProblemTag;
import kr.solta.domain.ReviewSchedule;
import kr.solta.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewScheduleService implements ReviewScheduleFinder, ReviewScheduleUpdater {

    private final ReviewScheduleRepository reviewScheduleRepository;
    private final MemberRepository memberRepository;
    private final ProblemTagRepository problemTagRepository;

    @Transactional(readOnly = true)
    @Override
    public ReviewListResponse findReviews(final String name) {
        Member member = getMemberByName(name);
        List<ReviewSchedule> schedules = reviewScheduleRepository.findAllPendingByMemberOrderByScheduledDateAsc(member);

        List<Problem> problems = schedules.stream().map(ReviewSchedule::getProblem).toList();
        Map<Problem, List<Tag>> tagsByProblem = getTagsByProblem(problems);

        List<List<String>> tagsList = schedules.stream()
                .map(s -> tagsByProblem.getOrDefault(s.getProblem(), List.of())
                        .stream()
                        .map(Tag::getKorName)
                        .toList())
                .toList();

        return ReviewListResponse.of(schedules, tagsList, LocalDate.now());
    }

    @Transactional(readOnly = true)
    @Override
    public ReviewHistoryResponse findCompletedReviews(final String name) {
        Member member = getMemberByName(name);
        List<ReviewSchedule> schedules = reviewScheduleRepository
                .findCompletedByMemberOrderByUpdatedAtDesc(member, PageRequest.of(0, 30));

        List<Problem> problems = schedules.stream().map(ReviewSchedule::getProblem).toList();
        Map<Problem, List<Tag>> tagsByProblem = getTagsByProblem(problems);

        List<List<String>> tagsList = schedules.stream()
                .map(s -> tagsByProblem.getOrDefault(s.getProblem(), List.of())
                        .stream()
                        .map(Tag::getKorName)
                        .toList())
                .toList();

        return ReviewHistoryResponse.of(schedules, tagsList);
    }

    @Override
    public void skip(final AuthMember authMember, final Long reviewScheduleId) {
        Member member = getMemberById(authMember.memberId());
        ReviewSchedule schedule = getScheduleById(reviewScheduleId);
        schedule.skip(member);
    }

    @Override
    public void reschedule(final AuthMember authMember, final Long reviewScheduleId, final int intervalDays) {
        Member member = getMemberById(authMember.memberId());
        ReviewSchedule schedule = getScheduleById(reviewScheduleId);
        schedule.reschedule(member, intervalDays, LocalDate.now());
    }

    @Override
    public void updateDefaultReviewInterval(final AuthMember authMember, final int intervalDays) {
        Member member = getMemberById(authMember.memberId());
        member.updateDefaultReviewInterval(intervalDays);
    }

    private ReviewSchedule getScheduleById(final Long reviewScheduleId) {
        return reviewScheduleRepository.findById(reviewScheduleId)
                .orElseThrow(() -> new NotFoundEntityException("존재하지 않는 복습 스케줄입니다."));
    }

    private Member getMemberById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException("존재하지 않는 사용자입니다."));
    }

    private Member getMemberByName(final String name) {
        return memberRepository.findByName(name)
                .orElseThrow(() -> new NotFoundEntityException("존재하지 않는 사용자입니다."));
    }

    private Map<Problem, List<Tag>> getTagsByProblem(final List<Problem> problems) {
        List<ProblemTag> problemTags = problemTagRepository.findByProblemsWithTag(problems);
        return problemTags.stream()
                .collect(Collectors.groupingBy(
                        ProblemTag::getProblem,
                        Collectors.mapping(ProblemTag::getTag, Collectors.toList())
                ));
    }
}
