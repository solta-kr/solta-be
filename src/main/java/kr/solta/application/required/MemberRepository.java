package kr.solta.application.required;

import java.util.List;
import java.util.Optional;
import kr.solta.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByBojId(final String bojId);

    Optional<Member> findByGithubId(final Long githubId);

    Optional<Member> findByName(final String name);

    @Query("SELECT m FROM Member m WHERE m.id > :lastMemberId ORDER BY m.id ASC")
    List<Member> findAllAfterMemberId(@Param("lastMemberId") long lastMemberId, Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.name LIKE CONCAT(:query, '%') AND m.id > :lastMemberId ORDER BY m.id ASC")
    List<Member> searchByNamePrefixAfterMemberId(@Param("query") String query, @Param("lastMemberId") long lastMemberId, Pageable pageable);
}
