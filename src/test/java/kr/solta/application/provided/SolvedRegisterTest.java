package kr.solta.application.provided;

import static kr.solta.support.TestFixtures.createMember;
import static kr.solta.support.TestFixtures.createProblem;
import static kr.solta.support.TestFixtures.createProblemTag;
import static kr.solta.support.TestFixtures.createTag;
import static org.assertj.core.api.Assertions.*;

import kr.solta.application.provided.request.AuthMember;
import kr.solta.application.provided.request.SolvedRegisterRequest;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.ProblemTagRepository;
import kr.solta.application.required.TagRepository;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.SolveType;
import kr.solta.domain.Solved;
import kr.solta.domain.Tag;
import kr.solta.support.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SolvedRegisterTest extends IntegrationTest {

    @Autowired
    private SolvedRegister solvedRegister;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private ProblemTagRepository problemTagRepository;

    @Test
    void solved를_등록할_수_있다() {
        //given
        Member member = memberRepository.save(createMember());
        Tag tag = tagRepository.save(createTag());
        Problem problem = problemRepository.save(createProblem());
        problemTagRepository.save(createProblemTag(problem, tag));
        SolvedRegisterRequest solvedRegisterRequest = new SolvedRegisterRequest(
                SolveType.SELF,
                problem.getBojProblemId(),
                1200,
                null
        );

        //when
        Solved solved = solvedRegister.register(new AuthMember(member.getId()), solvedRegisterRequest);

        //then
        assertThat(solved)
                .extracting(Solved::getMember, Solved::getProblem, Solved::getSolveType, Solved::getSolveTimeSeconds)
                .containsExactly(member, problem, SolveType.SELF, 1200);
    }

    @Test
    void solved를_등록할때_인증된_사용자가_존재하지_않는다면_예외가_발생한다() {
        //given
        Problem problem = problemRepository.save(createProblem());
        SolvedRegisterRequest solvedRegisterRequest = new SolvedRegisterRequest(
                SolveType.SELF,
                problem.getBojProblemId(),
                1200,
                null
        );
        AuthMember notExistMember = new AuthMember(999L);

        //when & then
        assertThatThrownBy(() -> solvedRegister.register(notExistMember, solvedRegisterRequest))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("존재하지 않는 사용자입니다");
    }

    @Test
    void solved를_등록할때_해당_문제가_존재하지_않는다면_예외가_발생한다() {
        //given
        Member member = memberRepository.save(createMember());
        SolvedRegisterRequest solvedRegisterRequest = new SolvedRegisterRequest(
                SolveType.SELF,
                9999L,
                1200,
                null
        );

        //when & then
        assertThatThrownBy(() -> solvedRegister.register(new AuthMember(member.getId()), solvedRegisterRequest))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("백준 문제 번호: 9999 에 해당하는 문제가 존재하지 않습니다");
    }

    @Test
    void 메모와_함께_solved를_등록할_수_있다() {
        //given
        Member member = memberRepository.save(createMember());
        Tag tag = tagRepository.save(createTag());
        Problem problem = problemRepository.save(createProblem());
        problemTagRepository.save(createProblemTag(problem, tag));
        String memo = "그리디 접근으로 풀었다.";
        SolvedRegisterRequest solvedRegisterRequest = new SolvedRegisterRequest(
                SolveType.SELF,
                problem.getBojProblemId(),
                1200,
                memo
        );

        //when
        Solved solved = solvedRegister.register(new AuthMember(member.getId()), solvedRegisterRequest);

        //then
        assertThat(solved.getMemo()).isEqualTo(memo);
    }
}
