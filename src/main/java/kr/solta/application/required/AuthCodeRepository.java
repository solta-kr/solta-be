package kr.solta.application.required;

import java.util.Optional;
import kr.solta.domain.AuthCode;
import kr.solta.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthCodeRepository extends JpaRepository<AuthCode, Long> {

    Optional<AuthCode> findByMember(Member member);
}
