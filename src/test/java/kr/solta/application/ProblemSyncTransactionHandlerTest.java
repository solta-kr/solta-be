package kr.solta.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import kr.solta.application.required.ProblemRepository;
import kr.solta.application.required.ProblemTagRepository;
import kr.solta.application.required.TagRepository;
import kr.solta.application.required.dto.SolvedAcDisplayName;
import kr.solta.application.required.dto.SolvedAcProblemResponse;
import kr.solta.application.required.dto.SolvedAcTagResponse;
import kr.solta.domain.Problem;
import kr.solta.domain.ProblemTag;
import kr.solta.domain.Tag;
import kr.solta.domain.Tier;
import kr.solta.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProblemSyncTransactionHandlerTest extends IntegrationTest {

    @Autowired
    private ProblemSyncTransactionHandler transactionHandler;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ProblemTagRepository problemTagRepository;

    @Test
    void 기존_문제의_티어가_변경되면_업데이트한다() {
        //given
        Problem problem = problemRepository.save(new Problem("피보나치 함수", 1003L, Tier.S3));
        SolvedAcProblemResponse response = new SolvedAcProblemResponse(1003L, "피보나치 함수", Tier.G5.getLevel(), List.of());

        //when
        int updatedCount = transactionHandler.updateExistingBatch(List.of(problem), List.of(response));

        //then
        assertSoftly(softly -> {
            softly.assertThat(updatedCount).isEqualTo(1);
            softly.assertThat(problem.getTier()).isEqualTo(Tier.G5);
        });
    }

    @Test
    void 기존_문제의_티어가_동일하면_업데이트하지_않는다() {
        //given
        Problem problem = problemRepository.save(new Problem("피보나치 함수", 1003L, Tier.S3));
        SolvedAcProblemResponse response = new SolvedAcProblemResponse(1003L, "피보나치 함수", Tier.S3.getLevel(), List.of());

        //when
        int updatedCount = transactionHandler.updateExistingBatch(List.of(problem), List.of(response));

        //then
        assertThat(updatedCount).isEqualTo(0);
    }

    @Test
    void 응답에_매칭되지_않는_문제는_건너뛴다() {
        //given
        Problem problem = problemRepository.save(new Problem("피보나치 함수", 1003L, Tier.S3));
        SolvedAcProblemResponse response = new SolvedAcProblemResponse(9999L, "다른 문제", Tier.G1.getLevel(), List.of());

        //when
        int updatedCount = transactionHandler.updateExistingBatch(List.of(problem), List.of(response));

        //then
        assertSoftly(softly -> {
            softly.assertThat(updatedCount).isEqualTo(0);
            softly.assertThat(problem.getTier()).isEqualTo(Tier.S3);
        });
    }

    @Test
    void 업데이트_시_태그를_동기화한다() {
        //given
        Problem problem = problemRepository.save(new Problem("피보나치 함수", 1003L, Tier.S3));
        SolvedAcTagResponse dpTag = new SolvedAcTagResponse("dp", 25, List.of(new SolvedAcDisplayName("ko", "다이나믹 프로그래밍")));
        SolvedAcProblemResponse response = new SolvedAcProblemResponse(1003L, "피보나치 함수", Tier.S3.getLevel(), List.of(dpTag));

        //when
        transactionHandler.updateExistingBatch(List.of(problem), List.of(response));

        //then
        List<ProblemTag> problemTags = problemTagRepository.findAllByProblemIn(List.of(problem));
        assertThat(problemTags).hasSize(1);
        assertThat(problemTags.getFirst().getTag().getKey()).isEqualTo("dp");
        assertThat(problemTags.getFirst().getTag().getKorName()).isEqualTo("다이나믹 프로그래밍");
    }

    @Test
    void 이미_존재하는_태그는_중복_추가하지_않는다() {
        //given
        Problem problem = problemRepository.save(new Problem("피보나치 함수", 1003L, Tier.S3));
        Tag existingTag = tagRepository.save(new Tag(25, "dp", "다이나믹 프로그래밍"));
        problemTagRepository.save(new ProblemTag(problem, existingTag));

        SolvedAcTagResponse dpTag = new SolvedAcTagResponse("dp", 25, List.of(new SolvedAcDisplayName("ko", "다이나믹 프로그래밍")));
        SolvedAcProblemResponse response = new SolvedAcProblemResponse(1003L, "피보나치 함수", Tier.S3.getLevel(), List.of(dpTag));

        //when
        transactionHandler.updateExistingBatch(List.of(problem), List.of(response));

        //then
        List<ProblemTag> problemTags = problemTagRepository.findAllByProblemIn(List.of(problem));
        assertThat(problemTags).hasSize(1);
    }

    @Test
    void 신규_문제를_등록한다() {
        //given
        SolvedAcProblemResponse response1 = new SolvedAcProblemResponse(1003L, "피보나치 함수", Tier.S3.getLevel(), List.of());
        SolvedAcProblemResponse response2 = new SolvedAcProblemResponse(1149L, "RGB거리", Tier.S1.getLevel(), List.of());

        //when
        int newCount = transactionHandler.insertNewBatch(List.of(response1, response2));

        //then
        assertThat(newCount).isEqualTo(2);

        List<Problem> problems = problemRepository.findAll();
        assertSoftly(softly -> {
            softly.assertThat(problems).hasSize(2);
            softly.assertThat(problems).extracting(Problem::getBojProblemId)
                    .containsExactlyInAnyOrder(1003L, 1149L);
            softly.assertThat(problems).extracting(Problem::getTier)
                    .containsExactlyInAnyOrder(Tier.S3, Tier.S1);
        });
    }

    @Test
    void 신규_문제_등록_시_태그도_함께_저장한다() {
        //given
        SolvedAcTagResponse dpTag = new SolvedAcTagResponse("dp", 25, List.of(new SolvedAcDisplayName("ko", "다이나믹 프로그래밍")));
        SolvedAcTagResponse mathTag = new SolvedAcTagResponse("math", 124, List.of(new SolvedAcDisplayName("ko", "수학")));
        SolvedAcProblemResponse response = new SolvedAcProblemResponse(1003L, "피보나치 함수", Tier.S3.getLevel(), List.of(dpTag, mathTag));

        //when
        transactionHandler.insertNewBatch(List.of(response));

        //then
        Problem savedProblem = problemRepository.findByBojProblemId(1003L).orElseThrow();
        List<ProblemTag> problemTags = problemTagRepository.findAllByProblemIn(List.of(savedProblem));

        assertSoftly(softly -> {
            softly.assertThat(problemTags).hasSize(2);
            softly.assertThat(problemTags).extracting(pt -> pt.getTag().getKey())
                    .containsExactlyInAnyOrder("dp", "math");
        });
    }

    @Test
    void 태그의_한국어_이름이_없으면_key를_사용한다() {
        //given
        SolvedAcTagResponse tag = new SolvedAcTagResponse("greedy", 33, List.of(new SolvedAcDisplayName("en", "Greedy")));
        SolvedAcProblemResponse response = new SolvedAcProblemResponse(1003L, "피보나치 함수", Tier.S3.getLevel(), List.of(tag));

        //when
        transactionHandler.insertNewBatch(List.of(response));

        //then
        Tag savedTag = tagRepository.findByKey("greedy").orElseThrow();
        assertThat(savedTag.getKorName()).isEqualTo("greedy");
    }

    @Test
    void 태그가_null이면_태그_동기화를_건너뛴다() {
        //given
        SolvedAcProblemResponse response = new SolvedAcProblemResponse(1003L, "피보나치 함수", Tier.S3.getLevel(), null);

        //when
        transactionHandler.insertNewBatch(List.of(response));

        //then
        Problem savedProblem = problemRepository.findByBojProblemId(1003L).orElseThrow();
        List<ProblemTag> problemTags = problemTagRepository.findAllByProblemIn(List.of(savedProblem));
        assertThat(problemTags).isEmpty();
    }

    @Test
    void 여러_문제를_한번에_업데이트한다() {
        //given
        Problem problem1 = problemRepository.save(new Problem("문제1", 1001L, Tier.S3));
        Problem problem2 = problemRepository.save(new Problem("문제2", 1002L, Tier.S1));
        Problem problem3 = problemRepository.save(new Problem("문제3", 1003L, Tier.G5));

        SolvedAcProblemResponse response1 = new SolvedAcProblemResponse(1001L, "문제1", Tier.G5.getLevel(), List.of());
        SolvedAcProblemResponse response2 = new SolvedAcProblemResponse(1002L, "문제2", Tier.S1.getLevel(), List.of());
        SolvedAcProblemResponse response3 = new SolvedAcProblemResponse(1003L, "문제3", Tier.G1.getLevel(), List.of());

        //when
        int updatedCount = transactionHandler.updateExistingBatch(
                List.of(problem1, problem2, problem3),
                List.of(response1, response2, response3)
        );

        //then
        assertSoftly(softly -> {
            softly.assertThat(updatedCount).isEqualTo(2);
            softly.assertThat(problem1.getTier()).isEqualTo(Tier.G5);
            softly.assertThat(problem2.getTier()).isEqualTo(Tier.S1);
            softly.assertThat(problem3.getTier()).isEqualTo(Tier.G1);
        });
    }
}