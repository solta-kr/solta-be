package kr.solta.domain;

import static java.util.Objects.requireNonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Solved extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer solveTimeSeconds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SolveType solveType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Problem problem;

    private LocalDateTime solvedTime;

    @Column(columnDefinition = "TEXT")
    private String memo;

    public static Solved register(
            final Integer solveTimeSeconds,
            final SolveType solveType,
            final Member member,
            final Problem problem,
            final LocalDateTime solvedTime,
            final String memo
    ) {
        Solved solved = new Solved();

        solved.solveTimeSeconds = solveType == SolveType.SELF ? requireNonNull(solveTimeSeconds, "스스로 푼 경우 풀이 시간은 필수입니다.") : solveTimeSeconds;
        solved.solveType = requireNonNull(solveType);
        solved.member = requireNonNull(member);
        solved.problem = requireNonNull(problem);
        solved.solvedTime = requireNonNull(solvedTime);
        solved.memo = memo;

        return solved;
    }

    public void updateMemo(final Member member, final String memo) {
        if (!this.member.equals(member)) {
            throw new IllegalArgumentException("본인의 풀이만 수정할 수 있습니다.");
        }
        this.memo = memo;
    }
}
