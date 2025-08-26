package kr.solta.application.required;

import kr.solta.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    Optional<Problem> findByBojProblemId(long bojProblemId);
}
