package kr.solta.domain;

import static kr.solta.support.TestFixtures.createMember;
import static kr.solta.support.TestFixtures.createProblem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class SolvedTest {

    @Test
    void 정답을_본_풀이인_경우_푼_시간이_null이될_수_있다() {
        //given //when
        Solved solved = Solved.register(
                null,
                SolveType.SOLUTION,
                createMember(),
                createProblem(),
                LocalDateTime.now(),
                null
        );

        //then
        assertThat(solved.getSolveTimeSeconds()).isNull();
        assertThat(solved.getSolveType()).isEqualTo(SolveType.SOLUTION);
    }

    @Test
    void 스스로_푼_풀이인_경우_푼_시간이_null이_될_수_없다() {
        //given //when //then
        assertThatThrownBy(() -> Solved.register(
                null,
                SolveType.SELF,
                createMember(),
                createProblem(),
                LocalDateTime.now(),
                null
        )).isInstanceOf(NullPointerException.class);
    }

    @Test
    void 메모와_함께_등록할_수_있다() {
        //given
        String memo = "이 문제는 BFS로 풀었다.";

        //when
        Solved solved = Solved.register(
                3600,
                SolveType.SELF,
                createMember(),
                createProblem(),
                LocalDateTime.now(),
                memo
        );

        //then
        assertThat(solved.getMemo()).isEqualTo(memo);
    }

    @Test
    void 메모_없이_등록하면_memo가_null이다() {
        //given //when
        Solved solved = Solved.register(
                3600,
                SolveType.SELF,
                createMember(),
                createProblem(),
                LocalDateTime.now(),
                null
        );

        //then
        assertThat(solved.getMemo()).isNull();
    }

    @Test
    void 본인_풀이의_메모를_업데이트할_수_있다() {
        //given
        Member member = createMember();
        Solved solved = Solved.register(3600, SolveType.SELF, member, createProblem(), LocalDateTime.now(), null);
        String newMemo = "업데이트된 메모";

        //when
        solved.updateMemo(member, newMemo);

        //then
        assertThat(solved.getMemo()).isEqualTo(newMemo);
    }

    @Test
    void 다른_사람의_풀이_메모를_업데이트하면_예외가_발생한다() {
        //given
        Member owner = createMember(1L, "owner");
        Member other = createMember(2L, "other");
        Solved solved = Solved.register(3600, SolveType.SELF, owner, createProblem(), LocalDateTime.now(), null);

        //when //then
        assertThatThrownBy(() -> solved.updateMemo(other, "악의적인 메모"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인의 풀이만 수정할 수 있습니다.");
    }
}
