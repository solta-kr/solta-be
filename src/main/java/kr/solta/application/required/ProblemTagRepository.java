package kr.solta.application.required;

import java.util.List;
import kr.solta.domain.Problem;
import kr.solta.domain.ProblemTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProblemTagRepository extends JpaRepository<ProblemTag, Long> {

    @Query("SELECT pt FROM ProblemTag pt JOIN FETCH pt.tag WHERE pt.problem IN :problems")
    List<ProblemTag> findByProblemsWithTag(@Param("problems") List<Problem> problems);
}
