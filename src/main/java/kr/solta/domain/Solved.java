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

    @Column(nullable = false)
    private int solveTimeSeconds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SolveType solveType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Problem problem;

    @Column(nullable = false)
    private LocalDateTime solvedTime;

    public static Solved register(
            int solveTimeSeconds,
            SolveType solveType,
            Member member,
            Problem problem,
            LocalDateTime solvedTime
    ) {
        Solved solved = new Solved();

        solved.solveTimeSeconds = solveTimeSeconds;
        solved.solveType = requireNonNull(solveType);
        solved.member = requireNonNull(member);
        solved.problem = requireNonNull(problem);
        solved.solvedTime = requireNonNull(solvedTime);

        return solved;
    }
}
