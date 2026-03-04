package kr.solta.application.provided;

import static kr.solta.support.TestFixtures.createMember;
import static kr.solta.support.TestFixtures.createProblem;
import static kr.solta.support.TestFixtures.createReviewSchedule;
import static kr.solta.support.TestFixtures.createSolved;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.ReviewScheduleRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.application.required.XpHistoryRepository;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.Solved;
import kr.solta.domain.SolveType;
import kr.solta.domain.Tier;
import kr.solta.domain.XpHistory;
import kr.solta.domain.XpSolveType;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class XpEarnerTest extends IntegrationTest {

    @Autowired private XpEarner xpEarner;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProblemRepository problemRepository;
    @Autowired private SolvedRepository solvedRepository;
    @Autowired private XpHistoryRepository xpHistoryRepository;
    @Autowired private ReviewScheduleRepository reviewScheduleRepository;

    @Test
    void XP를_획득하면_멤버의_totalXp와_level이_갱신된다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem("문제", 1000L, Tier.S1));
        Solved solved = solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem));

        //when
        int earned = xpEarner.earnXp(member.getId(), solved.getId());

        //then
        // S1 SELF 30분, 스트릭 없음 → baseXp = round(30 * 1.8 * 1.5) = 81
        assertThat(earned).isEqualTo(81);
        Member updated = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updated.getTotalXp()).isEqualTo(81);
        assertThat(updated.getLevel()).isEqualTo(1); // 81 < 150 → 레벨 1 유지
    }

    @Test
    void UNRATED_문제는_XP를_획득하지_못한다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem("문제", 1000L, Tier.UNRATED));
        Solved solved = solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem));

        //when
        int earned = xpEarner.earnXp(member.getId(), solved.getId());

        //then
        assertThat(earned).isEqualTo(0);
        assertThat(xpHistoryRepository.findByMemberOrderByCreatedAtDesc(member)).isEmpty();
        Member updated = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updated.getTotalXp()).isEqualTo(0);
    }

    @Test
    void XP_획득_내역이_히스토리에_저장된다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem("문제", 1000L, Tier.S1));
        Solved solved = solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem));

        //when
        xpEarner.earnXp(member.getId(), solved.getId());

        //then
        List<XpHistory> histories = xpHistoryRepository.findByMemberOrderByCreatedAtDesc(member);
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).getXpAmount()).isEqualTo(81);
        assertThat(histories.get(0).getSolveType()).isEqualTo(XpSolveType.SELF);
    }

    @Test
    void 복습_스케줄이_있으면_복습_타입으로_XP를_계산한다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem("문제", 1000L, Tier.S1));
        Solved originSolved = solvedRepository.save(createSolved(3600, SolveType.SOLUTION, member, problem));
        reviewScheduleRepository.save(createReviewSchedule(member, problem, originSolved));
        Solved reviewSolved = solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem));

        //when
        int earned = xpEarner.earnXp(member.getId(), reviewSolved.getId());

        //then
        // REVIEW_SELF, S1, 30분 → round(30 * 1.8 * 2.0) = 108
        assertThat(earned).isEqualTo(108);
        List<XpHistory> histories = xpHistoryRepository.findByMemberOrderByCreatedAtDesc(member);
        assertThat(histories.get(0).getSolveType()).isEqualTo(XpSolveType.REVIEW_SELF);
    }

    @Test
    void XP가_누적되어_임계값을_넘으면_레벨이_오른다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem("문제", 1000L, Tier.G1));
        // G1 SOLUTION: round(3.5 * 15.0) = 53. 3번 → 159XP → 레벨 2 (임계값 150)

        //when
        for (int i = 0; i < 3; i++) {
            Solved solved = solvedRepository.save(createSolved(3600, SolveType.SOLUTION, member, problem));
            xpEarner.earnXp(member.getId(), solved.getId());
        }

        //then
        Member updated = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updated.getTotalXp()).isEqualTo(159);
        assertThat(updated.getLevel()).isEqualTo(2);
    }
}
