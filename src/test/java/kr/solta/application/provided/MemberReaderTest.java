package kr.solta.application.provided;

import static kr.solta.support.TestFixtures.createMember;
import static kr.solta.support.TestFixtures.createProblem;
import static kr.solta.support.TestFixtures.createSolved;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import kr.solta.application.provided.response.MemberProfileResponse;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.SolveType;
import kr.solta.domain.Tier;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberReaderTest extends IntegrationTest {

    @Autowired
    private MemberReader memberReader;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SolvedRepository solvedRepository;

    @Test
    void 사용자_ID로_회원을_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));

        //when
        Member foundMember = memberReader.getMemberById(member.getId());

        //then
        assertThat(foundMember.getId()).isEqualTo(member.getId());
        assertThat(foundMember.getName()).isEqualTo("testUser");
    }

    @Test
    void 존재하지_않는_사용자_ID로_조회하면_예외가_발생한다() {
        //given
        Long notExistMemberId = 999L;

        //when & then
        assertThatThrownBy(() -> memberReader.getMemberById(notExistMemberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다");
    }

    @Test
    void 사용자_이름으로_프로필을_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "testUser"));

        Problem problem1 = problemRepository.save(createProblem("문제1", 1000L, Tier.B1));
        Problem problem2 = problemRepository.save(createProblem("문제2", 1001L, Tier.S3));
        Problem problem3 = problemRepository.save(createProblem("문제3", 1002L, Tier.G2));

        solvedRepository.save(createSolved(3600, SolveType.SELF, member, problem1));
        solvedRepository.save(createSolved(1800, SolveType.SELF, member, problem2));
        solvedRepository.save(createSolved(5400, SolveType.SELF, member, problem3));

        //when
        MemberProfileResponse profile = memberReader.getMemberProfile("testUser");

        //then
        assertSoftly(softly -> {
            softly.assertThat(profile.memberId()).isEqualTo(member.getId());
            softly.assertThat(profile.name()).isEqualTo("testUser");
            softly.assertThat(profile.solvedCount()).isEqualTo(3);
            softly.assertThat(profile.totalSolvedTime()).isEqualTo(10800.0);
            softly.assertThat(profile.totalSolvedAverageTime()).isEqualTo(3600.0);
        });
    }

    @Test
    void 문제를_풀지_않은_사용자의_프로필을_조회할_수_있다() {
        //given
        Member member = memberRepository.save(createMember(1L, "newUser"));

        //when
        MemberProfileResponse profile = memberReader.getMemberProfile("newUser");

        //then
        assertSoftly(softly -> {
            softly.assertThat(profile.memberId()).isEqualTo(member.getId());
            softly.assertThat(profile.name()).isEqualTo("newUser");
            softly.assertThat(profile.solvedCount()).isEqualTo(0);
            softly.assertThat(profile.totalSolvedTime()).isEqualTo(0.0);
            softly.assertThat(profile.totalSolvedAverageTime()).isEqualTo(0.0);
        });
    }

    @Test
    void 존재하지_않는_사용자_이름으로_프로필_조회시_예외가_발생한다() {
        //given
        String notExistUserName = "notExistUser";

        //when & then
        assertThatThrownBy(() -> memberReader.getMemberProfile(notExistUserName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다");
    }
}