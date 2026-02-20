package kr.solta.support;

import java.time.LocalDateTime;
import kr.solta.domain.Member;
import kr.solta.domain.Problem;
import kr.solta.domain.ProblemTag;
import kr.solta.domain.SolveType;
import kr.solta.domain.Solved;
import kr.solta.domain.Tag;
import kr.solta.domain.Tier;

public class TestFixtures {

    public static Member createMember() {
        return Member.create(1L, "testUser", "https://avatar.example.com/test.png");
    }

    public static Member createMember(Long githubId, String name) {
        return Member.create(githubId, name, "https://avatar.example.com/" + name + ".png");
    }

    public static Member createMember(Long githubId, String name, String avatarUrl) {
        return Member.create(githubId, name, avatarUrl);
    }

    public static Problem createProblem() {
        return new Problem("테스트 문제", 1000L, Tier.B1);
    }

    public static Problem createProblem(String title, long bojProblemId) {
        return new Problem(title, bojProblemId, Tier.B1);
    }

    public static Problem createProblem(String title, long bojProblemId, Tier tier) {
        return new Problem(title, bojProblemId, tier);
    }

    public static Tag createTag() {
        return new Tag(1, "implementation", "구현");
    }

    public static Tag createTag(int id, String key) {
        return new Tag(id, key, "테스트태그");
    }

    public static Tag createTag(int id, String key, String korName) {
        return new Tag(id, key, korName);
    }

    public static ProblemTag createProblemTag(Problem problem, Tag tag) {
        return new ProblemTag(problem, tag);
    }

    public static Solved createSolved(Member member, Problem problem) {
        return Solved.register(3600, SolveType.SELF, member, problem, LocalDateTime.now(), null);
    }

    public static Solved createSolved(int solveTimeSeconds, Member member, Problem problem) {
        return Solved.register(solveTimeSeconds, SolveType.SELF, member, problem, LocalDateTime.now(), null);
    }

    public static Solved createSolved(int solveTimeSeconds, SolveType solveType, Member member, Problem problem) {
        return Solved.register(solveTimeSeconds, solveType, member, problem, LocalDateTime.now(), null);
    }

    public static Solved createSolved(
            int solveTimeSeconds,
            SolveType solveType,
            Member member,
            Problem problem,
            LocalDateTime solvedTime
    ) {
        return Solved.register(solveTimeSeconds, solveType, member, problem, solvedTime, null);
    }
}