package kr.solta.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Solved originSolved;

    @Column(nullable = false)
    private LocalDate scheduledDate;

    @Column(nullable = false)
    private int round;

    @Column(nullable = false)
    private int intervalDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status;

    public static ReviewSchedule create(
            final Member member,
            final Problem problem,
            final Solved originSolved,
            final LocalDate today
    ) {
        int intervalDays = member.getEffectiveReviewInterval();
        ReviewSchedule schedule = new ReviewSchedule();
        schedule.member = member;
        schedule.problem = problem;
        schedule.originSolved = originSolved;
        schedule.scheduledDate = today.plusDays(intervalDays);
        schedule.round = 1;
        schedule.intervalDays = intervalDays;
        schedule.status = ReviewStatus.PENDING;
        return schedule;
    }

    public void advanceRound(final LocalDate today) {
        requirePending();
        int newIntervalDays = Math.min(this.intervalDays * 2, 14);
        this.intervalDays = newIntervalDays;
        this.scheduledDate = today.plusDays(newIntervalDays);
        this.round = this.round + 1;
    }

    public void complete() {
        requirePending();
        this.status = ReviewStatus.COMPLETED;
    }

    public void skip(final Member requestMember) {
        requireOwner(requestMember);
        requirePending();
        this.scheduledDate = this.scheduledDate.plusDays(3);
    }

    public void reschedule(final Member requestMember, final int newIntervalDays, final LocalDate today) {
        requireOwner(requestMember);
        requirePending();
        if (newIntervalDays == 0) {
            this.status = ReviewStatus.DISMISSED;
            return;
        }
        this.intervalDays = newIntervalDays;
        this.scheduledDate = today.plusDays(newIntervalDays);
    }

    public void dismiss() {
        this.status = ReviewStatus.DISMISSED;
    }

    private void requireOwner(final Member requestMember) {
        if (!this.member.equals(requestMember)) {
            throw new IllegalArgumentException("본인의 복습 스케줄만 수정할 수 있습니다.");
        }
    }

    private void requirePending() {
        if (this.status != ReviewStatus.PENDING) {
            throw new IllegalArgumentException("PENDING 상태의 복습 스케줄만 수정할 수 있습니다.");
        }
    }
}
