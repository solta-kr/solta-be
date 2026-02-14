package kr.solta.application.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.ProblemTagRepository;
import kr.solta.application.required.SolvedAcClient;
import kr.solta.application.required.TagRepository;
import kr.solta.application.required.dto.SolvedAcDisplayName;
import kr.solta.application.required.dto.SolvedAcProblemResponse;
import kr.solta.application.required.dto.SolvedAcTagResponse;
import kr.solta.domain.Problem;
import kr.solta.domain.ProblemTag;
import kr.solta.domain.Tier;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class ProblemSynchronizerTest extends IntegrationTest {

    @Autowired
    private ProblemSynchronizer problemSynchronizer;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private ProblemTagRepository problemTagRepository;

    @Autowired
    private TagRepository tagRepository;

    @MockitoBean
    private SolvedAcClient solvedAcClient;

    @Test
    void 기존_문제의_티어_변경을_반영한다() {
        //given
        problemRepository.save(new Problem("피보나치 함수", 1003L, Tier.S3));

        SolvedAcProblemResponse response = new SolvedAcProblemResponse(1003L, "피보나치 함수", Tier.G5.getLevel(), List.of());
        given(solvedAcClient.lookupProblems(anyList()))
                .willReturn(List.of(response))
                .willReturn(Collections.emptyList());

        //when
        problemSynchronizer.syncAll();

        //then
        Problem updated = problemRepository.findByBojProblemId(1003L).orElseThrow();
        assertThat(updated.getTier()).isEqualTo(Tier.G5);
    }

    @Test
    void 기존_문제에_새로운_태그가_추가되면_반영한다() {
        //given
        problemRepository.save(new Problem("피보나치 함수", 1003L, Tier.S3));

        SolvedAcTagResponse dpTag = new SolvedAcTagResponse("dp", 25, List.of(new SolvedAcDisplayName("ko", "다이나믹 프로그래밍")));
        SolvedAcProblemResponse response = new SolvedAcProblemResponse(1003L, "피보나치 함수", Tier.S3.getLevel(), List.of(dpTag));
        given(solvedAcClient.lookupProblems(anyList()))
                .willReturn(List.of(response))
                .willReturn(Collections.emptyList());

        //when
        problemSynchronizer.syncAll();

        //then
        Problem problem = problemRepository.findByBojProblemId(1003L).orElseThrow();
        List<ProblemTag> problemTags = problemTagRepository.findAllByProblemIn(List.of(problem));
        assertSoftly(softly -> {
            softly.assertThat(problemTags).hasSize(1);
            softly.assertThat(problemTags.getFirst().getTag().getKey()).isEqualTo("dp");
            softly.assertThat(problemTags.getFirst().getTag().getKorName()).isEqualTo("다이나믹 프로그래밍");
        });
    }

    @Test
    void 새로_생긴_문제를_등록한다() {
        //given
        SolvedAcTagResponse mathTag = new SolvedAcTagResponse("math", 124, List.of(new SolvedAcDisplayName("ko", "수학")));
        SolvedAcProblemResponse response = new SolvedAcProblemResponse(1001L, "새 문제", Tier.G1.getLevel(), List.of(mathTag));

        given(solvedAcClient.lookupProblems(anyList()))
                .willReturn(List.of(response))
                .willReturn(Collections.emptyList());

        //when
        problemSynchronizer.syncAll();

        //then
        Problem saved = problemRepository.findByBojProblemId(1001L).orElseThrow();
        List<ProblemTag> problemTags = problemTagRepository.findAllByProblemIn(List.of(saved));
        assertSoftly(softly -> {
            softly.assertThat(saved.getTitle()).isEqualTo("새 문제");
            softly.assertThat(saved.getTier()).isEqualTo(Tier.G1);
            softly.assertThat(problemTags).hasSize(1);
            softly.assertThat(problemTags.getFirst().getTag().getKey()).isEqualTo("math");
        });
    }

    @Test
    void API_호출이_실패해도_동기화를_중단하지_않는다() {
        //given
        problemRepository.save(new Problem("문제1", 1001L, Tier.S3));

        given(solvedAcClient.lookupProblems(anyList()))
                .willThrow(new RuntimeException("API 오류"))
                .willReturn(Collections.emptyList());

        //when & then (예외 없이 완료)
        problemSynchronizer.syncAll();

        Problem problem = problemRepository.findByBojProblemId(1001L).orElseThrow();
        assertThat(problem.getTier()).isEqualTo(Tier.S3);
    }
}
