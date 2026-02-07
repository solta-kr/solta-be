package kr.solta.application.required;

import java.time.LocalDateTime;
import java.util.List;
import kr.solta.application.required.dto.AllSolvedAverage;
import kr.solta.application.required.dto.IndependentRatioData;
import kr.solta.application.required.dto.ProblemSolvedStats;
import kr.solta.application.required.dto.SolvedStats;
import kr.solta.application.required.dto.TrendData;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.SolveType;
import kr.solta.domain.Solved;
import kr.solta.domain.SolvedAverage;
import kr.solta.domain.Tier;
import kr.solta.domain.TierAverage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SolvedRepository extends JpaRepository<Solved, Long> {

    @EntityGraph(attributePaths = "problem")
    List<Solved> findByMemberOrderByCreatedAtDesc(Member member);

    @Query("""
                select new kr.solta.domain.SolvedAverage(s.problem, avg(s.solveTimeSeconds))
                from Solved s
                where s.problem in :problems
                group by s.problem.id
            """)
    List<SolvedAverage> findSolvedAveragesByProblems(List<Problem> problems);

    @Query("""
                select new kr.solta.application.required.dto.SolvedStats(
                    count(s.id),
                    avg(s.solveTimeSeconds),
                    coalesce(sum(case when s.solveType = kr.solta.domain.SolveType.SELF then 1 else 0 end), 0)
                )
                from Solved s
                where s.member = :member and s.problem.tier in :tiers
            """)
    SolvedStats calculateTierGroupAverageByMember(Member member, List<Tier> tiers);

    @Query("""
                select new kr.solta.application.required.dto.SolvedStats(
                    count(distinct s.id),
                    avg(s.solveTimeSeconds),
                    coalesce(sum(case when s.solveType = kr.solta.domain.SolveType.SELF then 1 else 0 end), 0)
                )
                from Solved s
                join s.problem p
                join ProblemTag pt on pt.problem = p
                join pt.tag t
                where s.member = :member and s.problem.tier in :tiers and t.key = :tagKey
            """)
    SolvedStats calculateTierGroupAverageByMemberAndTag(Member member, List<Tier> tiers, String tagKey);

    @Query("""
                select new kr.solta.domain.TierAverage(s.problem.tier, avg(s.solveTimeSeconds), count(s.id))
                from Solved s
                where s.member = :member
                group by s.problem.tier
            """)
    List<TierAverage> findTierAverageByMember(Member member);

    @Query("""
                select new kr.solta.application.required.dto.AllSolvedAverage(count(s.id), sum (s.solveTimeSeconds), avg(s.solveTimeSeconds))
                from Solved s
                where s.member = :member
            """)
    AllSolvedAverage findAllSolvedAverage(Member member);

    @Query("""
                SELECT new kr.solta.application.required.dto.TrendData(
                    CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string),
                    AVG(s.solveTimeSeconds),
                    COUNT(s.id)
                )
                FROM Solved s
                WHERE s.member.id = :memberId
                AND (:startDate IS NULL OR s.solvedTime >= :startDate)
                GROUP BY CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string)
                ORDER BY CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string)
            """)
    List<TrendData> findSolveTimeTrendsAll(
            final Long memberId,
            final LocalDateTime startDate,
            final String dateFormat
    );

    @Query("""
                SELECT new kr.solta.application.required.dto.TrendData(
                    CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string),
                    AVG(s.solveTimeSeconds),
                    COUNT(s.id)
                )
                FROM Solved s
                WHERE s.member.id = :memberId
                AND (:startDate IS NULL OR s.solvedTime >= :startDate)
                AND s.problem.tier IN :tiers
                GROUP BY CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string)
                ORDER BY CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string)
            """)
    List<TrendData> findSolveTimeTrendsByTiers(
            final Long memberId,
            final LocalDateTime startDate,
            final String dateFormat,
            final List<Tier> tiers
    );

    @Query("""
                SELECT COUNT(s.id)
                FROM Solved s
                WHERE s.member.id = :memberId
                AND (:startDate IS NULL OR s.solvedTime >= :startDate)
            """)
    Long countSolvedByPeriod(
            final Long memberId,
            final LocalDateTime startDate
    );

    @Query("""
                SELECT COUNT(s.id)
                FROM Solved s
                WHERE s.member.id = :memberId
                AND (:startDate IS NULL OR s.solvedTime >= :startDate)
                AND s.problem.tier IN :tiers
            """)
    Long countSolvedByPeriodAndTiers(
            final Long memberId,
            final LocalDateTime startDate,
            final List<Tier> tiers
    );

    @Query("""
                SELECT new kr.solta.application.required.dto.TrendData(
                    CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string),
                    AVG(s.solveTimeSeconds),
                    COUNT(s.id)
                )
                FROM Solved s
                JOIN s.problem p
                JOIN ProblemTag pt ON pt.problem = p
                JOIN pt.tag t
                WHERE s.member.id = :memberId
                AND (:startDate IS NULL OR s.solvedTime >= :startDate)
                AND t.key = :tagKey
                GROUP BY CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string)
                ORDER BY CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string)
            """)
    List<TrendData> findSolveTimeTrendsByTag(
            final Long memberId,
            final LocalDateTime startDate,
            final String dateFormat,
            final String tagKey
    );

    @Query("""
                SELECT new kr.solta.application.required.dto.TrendData(
                    CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string),
                    AVG(s.solveTimeSeconds),
                    COUNT(s.id)
                )
                FROM Solved s
                JOIN s.problem p
                JOIN ProblemTag pt ON pt.problem = p
                JOIN pt.tag t
                WHERE s.member.id = :memberId
                AND (:startDate IS NULL OR s.solvedTime >= :startDate)
                AND s.problem.tier IN :tiers
                AND t.key = :tagKey
                GROUP BY CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string)
                ORDER BY CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string)
            """)
    List<TrendData> findSolveTimeTrendsByTiersAndTag(
            final Long memberId,
            final LocalDateTime startDate,
            final String dateFormat,
            final List<Tier> tiers,
            final String tagKey
    );

    @Query("""
                SELECT COUNT(DISTINCT s.id)
                FROM Solved s
                JOIN s.problem p
                JOIN ProblemTag pt ON pt.problem = p
                JOIN pt.tag t
                WHERE s.member.id = :memberId
                AND (:startDate IS NULL OR s.solvedTime >= :startDate)
                AND t.key = :tagKey
            """)
    Long countSolvedByPeriodAndTag(
            final Long memberId,
            final LocalDateTime startDate,
            final String tagKey
    );

    @Query("""
                SELECT COUNT(DISTINCT s.id)
                FROM Solved s
                JOIN s.problem p
                JOIN ProblemTag pt ON pt.problem = p
                JOIN pt.tag t
                WHERE s.member.id = :memberId
                AND (:startDate IS NULL OR s.solvedTime >= :startDate)
                AND s.problem.tier IN :tiers
                AND t.key = :tagKey
            """)
    Long countSolvedByPeriodAndTiersAndTag(
            final Long memberId,
            final LocalDateTime startDate,
            final List<Tier> tiers,
            final String tagKey
    );

    @Query("""
                SELECT new kr.solta.application.required.dto.IndependentRatioData(
                    CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string),
                    SUM(CASE WHEN s.solveType = kr.solta.domain.SolveType.SELF THEN 1 ELSE 0 END),
                    COUNT(s.id)
                )
                FROM Solved s
                WHERE s.member.id = :memberId
                AND (:startDate IS NULL OR s.solvedTime >= :startDate)
                GROUP BY CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string)
                ORDER BY CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string)
            """)
    List<IndependentRatioData> findIndependentRatioTrendsAll(
            final Long memberId,
            final LocalDateTime startDate,
            final String dateFormat
    );

    @Query("""
                SELECT new kr.solta.application.required.dto.IndependentRatioData(
                    CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string) as date,
                    SUM(CASE WHEN s.solveType = kr.solta.domain.SolveType.SELF THEN 1 ELSE 0 END),
                    COUNT(s.id)
                )
                FROM Solved s
                WHERE s.member.id = :memberId
                AND (:startDate IS NULL OR s.solvedTime >= :startDate)
                AND s.problem.tier IN :tiers
                GROUP BY CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string)
                ORDER BY CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string)
            """)
    List<IndependentRatioData> findIndependentRatioTrendsByTiers(
            final Long memberId,
            final LocalDateTime startDate,
            final String dateFormat,
            final List<Tier> tiers
    );

    @Query("""
                SELECT new kr.solta.application.required.dto.IndependentRatioData(
                    CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string),
                    SUM(CASE WHEN s.solveType = kr.solta.domain.SolveType.SELF THEN 1 ELSE 0 END),
                    COUNT(s.id)
                )
                FROM Solved s
                JOIN s.problem p
                JOIN ProblemTag pt ON pt.problem = p
                JOIN pt.tag t
                WHERE s.member.id = :memberId
                AND (:startDate IS NULL OR s.solvedTime >= :startDate)
                AND t.key = :tagKey
                GROUP BY CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string)
                ORDER BY CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string)
            """)
    List<IndependentRatioData> findIndependentRatioTrendsByTag(
            final Long memberId,
            final LocalDateTime startDate,
            final String dateFormat,
            final String tagKey
    );

    @Query("""
                SELECT new kr.solta.application.required.dto.IndependentRatioData(
                    CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string),
                    SUM(CASE WHEN s.solveType = kr.solta.domain.SolveType.SELF THEN 1 ELSE 0 END),
                    COUNT(s.id)
                )
                FROM Solved s
                JOIN s.problem p
                JOIN ProblemTag pt ON pt.problem = p
                JOIN pt.tag t
                WHERE s.member.id = :memberId
                AND (:startDate IS NULL OR s.solvedTime >= :startDate)
                AND s.problem.tier IN :tiers
                AND t.key = :tagKey
                GROUP BY CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string)
                ORDER BY CAST(FUNCTION('DATE_FORMAT', s.solvedTime, :dateFormat) AS string)
            """)
    List<IndependentRatioData> findIndependentRatioTrendsByTiersAndTag(
            final Long memberId,
            final LocalDateTime startDate,
            final String dateFormat,
            final List<Tier> tiers,
            final String tagKey
    );

    @Query("""
                SELECT new kr.solta.application.required.dto.ProblemSolvedStats(
                    count(s.id),
                    coalesce(sum(case when s.solveType = kr.solta.domain.SolveType.SELF then 1 else 0 end), 0),
                    avg(s.solveTimeSeconds),
                    min(s.solveTimeSeconds)
                )
                FROM Solved s
                WHERE s.problem = :problem
            """)
    ProblemSolvedStats findProblemSolvedStats(Problem problem);

    @EntityGraph(attributePaths = "problem")
    List<Solved> findByMemberAndSolveTypeOrderBySolvedTimeDesc(final Member member, final SolveType solveType);

    @Query("""
                SELECT s FROM Solved s
                JOIN FETCH s.problem p
                WHERE s.member = :member
                AND s.solveType = :solveType
                ORDER BY p.level DESC
            """)
    List<Solved> findByMemberAndSolveTypeOrderByLevelDesc(final Member member, final SolveType solveType);

    @EntityGraph(attributePaths = "problem")
    List<Solved> findByMemberAndSolveTypeOrderBySolveTimeSecondsDesc(final Member member, final SolveType solveType);
}
