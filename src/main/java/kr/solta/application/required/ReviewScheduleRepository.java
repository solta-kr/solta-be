package kr.solta.application.required;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.ReviewSchedule;
import kr.solta.domain.ReviewStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewScheduleRepository extends JpaRepository<ReviewSchedule, Long> {

    Optional<ReviewSchedule> findByMemberAndProblemAndStatus(Member member, Problem problem, ReviewStatus status);

    @EntityGraph(attributePaths = {"problem"})
    @Query("SELECT r FROM ReviewSchedule r WHERE r.member = :member AND r.status = 'PENDING' ORDER BY r.scheduledDate ASC")
    List<ReviewSchedule> findAllPendingByMemberOrderByScheduledDateAsc(@Param("member") Member member);

    @Query("SELECT r FROM ReviewSchedule r WHERE r.status = 'PENDING' AND r.scheduledDate <= :thresholdDate")
    List<ReviewSchedule> findAllPendingOlderThanOrEqual(@Param("thresholdDate") LocalDate thresholdDate);

    @EntityGraph(attributePaths = {"problem"})
    @Query("SELECT r FROM ReviewSchedule r WHERE r.member = :member AND r.status = 'COMPLETED' ORDER BY r.updatedAt DESC")
    List<ReviewSchedule> findCompletedByMemberOrderByUpdatedAtDesc(@Param("member") Member member, org.springframework.data.domain.Pageable pageable);
}
