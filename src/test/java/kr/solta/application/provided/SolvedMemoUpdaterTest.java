package kr.solta.application.provided;

import static kr.solta.support.TestFixtures.createMember;
import static kr.solta.support.TestFixtures.createProblem;
import static kr.solta.support.TestFixtures.createSolved;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import kr.solta.application.provided.request.AuthMember;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.Solved;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SolvedMemoUpdaterTest extends IntegrationTest {

    @Autowired
    private SolvedMemoUpdater solvedMemoUpdater;

    @Autowired
    private SolvedRepository solvedRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Test
    void 본인_풀이의_메모를_업데이트할_수_있다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem());
        Solved solved = solvedRepository.save(createSolved(member, problem));
        String memo = "BFS로 접근했다.";

        //when
        solvedMemoUpdater.update(new AuthMember(member.getId()), solved.getId(), memo);

        //then
        assertThat(solvedRepository.findById(solved.getId()).get().getMemo()).isEqualTo(memo);
    }

    @Test
    void 메모를_수정할_수_있다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem());
        Solved solved = solvedRepository.save(createSolved(member, problem));
        solvedMemoUpdater.update(new AuthMember(member.getId()), solved.getId(), "기존 메모");

        //when
        String updatedMemo = "수정된 메모";
        solvedMemoUpdater.update(new AuthMember(member.getId()), solved.getId(), updatedMemo);

        //then
        assertThat(solvedRepository.findById(solved.getId()).get().getMemo()).isEqualTo(updatedMemo);
    }

    @Test
    void 메모를_null로_초기화할_수_있다() {
        //given
        Member member = memberRepository.save(createMember());
        Problem problem = problemRepository.save(createProblem());
        Solved solved = solvedRepository.save(createSolved(member, problem));
        solvedMemoUpdater.update(new AuthMember(member.getId()), solved.getId(), "삭제할 메모");

        //when
        solvedMemoUpdater.update(new AuthMember(member.getId()), solved.getId(), null);

        //then
        assertThat(solvedRepository.findById(solved.getId()).get().getMemo()).isNull();
    }

    @Test
    void 존재하지_않는_풀이_메모_업데이트시_예외가_발생한다() {
        //given
        Member member = memberRepository.save(createMember());
        Long notExistSolvedId = 999L;

        //when & then
        assertThatThrownBy(() -> solvedMemoUpdater.update(new AuthMember(member.getId()), notExistSolvedId, "메모"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void 다른_사람의_풀이_메모를_업데이트하면_예외가_발생한다() {
        //given
        Member owner = memberRepository.save(createMember(1L, "owner"));
        Member other = memberRepository.save(createMember(2L, "other"));
        Problem problem = problemRepository.save(createProblem());
        Solved solved = solvedRepository.save(createSolved(owner, problem));

        //when & then
        assertThatThrownBy(() -> solvedMemoUpdater.update(new AuthMember(other.getId()), solved.getId(), "악의적인 메모"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인의 풀이만 수정할 수 있습니다.");
    }
}
