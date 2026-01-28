package kr.solta.application.required;

import java.util.Optional;
import kr.solta.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByBojId(final String bojId);

    Optional<Member> findByGithubId(final Long githubId);
}
