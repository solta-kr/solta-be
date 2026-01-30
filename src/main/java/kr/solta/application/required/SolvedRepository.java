package kr.solta.application.required;

import java.util.List;
import kr.solta.application.required.dto.AllSolvedAverage;
import kr.solta.application.required.dto.SolvedStats;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
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
                select new kr.solta.application.required.dto.SolvedStats(count(s.id), avg(s.solveTimeSeconds))
                from Solved s
                where s.member = :member and s.problem.tier in :tiers
            """)
    SolvedStats calculateTierGroupAverageByMember(Member member, List<Tier> tiers);

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
}
