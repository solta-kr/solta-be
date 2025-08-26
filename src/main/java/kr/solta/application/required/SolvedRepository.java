package kr.solta.application.required;

import kr.solta.domain.Solved;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolvedRepository extends JpaRepository<Solved, Long> {

}
