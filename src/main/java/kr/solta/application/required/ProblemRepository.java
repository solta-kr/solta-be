package kr.solta.application.required;

import java.util.Optional;
import kr.solta.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    Optional<Problem> findByBojProblemId(long bojProblemId);
}
