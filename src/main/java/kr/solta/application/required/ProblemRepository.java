package kr.solta.application.required;

import java.util.List;
import java.util.Optional;
import kr.solta.domain.Problem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    Optional<Problem> findByBojProblemId(long bojProblemId);

    @Query("SELECT p FROM Problem p WHERE p.bojProblemId > :lastBojProblemId ORDER BY p.bojProblemId ASC")
    List<Problem> findAllAfterBojProblemId(@Param("lastBojProblemId") long lastBojProblemId, Pageable pageable);

    @Query("SELECT p FROM Problem p WHERE str(p.bojProblemId) LIKE CONCAT(:query, '%') AND p.bojProblemId > :lastBojProblemId ORDER BY p.bojProblemId ASC")
    List<Problem>
    searchByBojProblemIdPrefixAfter(@Param("query") String query, @Param("lastBojProblemId") long lastBojProblemId,
                                    Pageable pageable);

    @Query("SELECT COALESCE(MAX(p.bojProblemId), 0) FROM Problem p")
    long findMaxBojProblemId();
}
