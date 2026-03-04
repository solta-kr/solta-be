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
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "xp_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class XpHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Solved solved;

    @Column(nullable = false)
    private int xpAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private XpSolveType solveType;

    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal tierWeight;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal streakBonus;

    public static XpHistory create(final Member member, final Solved solved,
                                   final XpSolveType solveType, final int streak) {
        Tier tier = solved.getProblem().getTier();
        BigDecimal tierWeight = BigDecimal.valueOf(tier.getXpWeight());
        int baseXp = solveType.calculateBaseXp(tier, solved.getSolveTimeSeconds());
        BigDecimal streakBonus = StreakBonus.of(streak);
        int earnedXp = (int) Math.round(baseXp * (1.0 + streakBonus.doubleValue()));

        XpHistory history = new XpHistory();
        history.member = member;
        history.solved = solved;
        history.xpAmount = earnedXp;
        history.solveType = solveType;
        history.tierWeight = tierWeight;
        history.streakBonus = streakBonus;
        return history;
    }
}
