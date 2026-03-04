package kr.solta.application.required;

import java.time.LocalDateTime;
import java.util.List;
import kr.solta.domain.Member;
import kr.solta.domain.XpHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XpHistoryRepository extends JpaRepository<XpHistory, Long> {

    List<XpHistory> findByMemberAndCreatedAtBetweenOrderByCreatedAtDesc(
            final Member member, final LocalDateTime start, final LocalDateTime end);

    List<XpHistory> findByMemberOrderByCreatedAtDesc(final Member member);
}
