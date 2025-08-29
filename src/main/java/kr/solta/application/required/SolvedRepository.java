package kr.solta.application.required;

import java.util.List;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.Solved;
import kr.solta.domain.SolvedAverage;
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
}
