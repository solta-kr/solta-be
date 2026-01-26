package kr.solta.application.provided;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import kr.solta.application.provided.response.SolvedWithTags;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.ProblemTagRepository;
import kr.solta.application.required.SolvedRepository;
import kr.solta.application.required.TagRepository;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.ProblemTag;
import kr.solta.domain.SolveType;
import kr.solta.domain.Solved;
import kr.solta.domain.Tag;
import kr.solta.domain.Tier;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SolvedFinderTest extends IntegrationTest {

    @Autowired
    private SolvedFinder solvedFinder;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ProblemTagRepository problemTagRepository;

    @Autowired
    private SolvedRepository solvedRepository;

    @Test
    void 사용자가_푼_풀이를_문제_태그와_함께_조회할_수_있다() {
        //given
        String bojId = "testUser";
        Member member = memberRepository.save(Member.create(bojId));

        Problem problem1 = createProblem("문제1", 1000L, Tier.B1);
        Problem problem2 = createProblem("문제2", 1001L, Tier.S3);

        Tag tag1 = createTag(1, "implementation", "구현");
        Tag tag2 = createTag(2, "math", "수학");
        Tag tag3 = createTag(3, "dp", "다이나믹 프로그래밍");

        createProblemTag(problem1, tag1);
        createProblemTag(problem1, tag2);
        createProblemTag(problem2, tag2);
        createProblemTag(problem2, tag3);

        Solved solved1 = solvedRepository.save(Solved.register(3600, SolveType.SELF, member, problem1));
        Solved solved2 = solvedRepository.save(Solved.register(1800, SolveType.SOLUTION, member, problem2));

        //when
        List<SolvedWithTags> result = solvedFinder.findSolvedWithTags(bojId);

        //then
        assertThat(result).hasSize(2)
                .containsExactly(
                        new SolvedWithTags(solved2, List.of(tag2, tag3)),
                        new SolvedWithTags(solved1, List.of(tag1, tag2))
                );
    }

    private Problem createProblem(String title, long bojProblemId, Tier tier) {
        return problemRepository.save(new Problem(title, bojProblemId, tier));
    }

    private Tag createTag(int id, String key, String korName) {
        return tagRepository.save(new Tag(id, key, korName));
    }

    private ProblemTag createProblemTag(Problem problem, Tag tag) {
        return problemTagRepository.save(new ProblemTag(problem, tag));
    }
}
