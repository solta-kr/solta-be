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
                LocalDateTime.now()
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
                LocalDateTime.now()
        )).isInstanceOf(NullPointerException.class);
    }
}
