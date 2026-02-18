package kr.solta.adapter.webapi;

import kr.solta.adapter.webapi.response.ProblemDetailResponse;
import kr.solta.adapter.webapi.response.ProblemSearchResponse;
import kr.solta.application.provided.ProblemFinder;
import kr.solta.application.provided.SolvedStatisticsReader;
import kr.solta.application.provided.response.ProblemDetail;
import kr.solta.application.provided.response.ProblemPage;
import kr.solta.application.provided.response.SolveTimeDistributionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/problems")
@RestController
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemFinder problemFinder;
    private final SolvedStatisticsReader solvedStatisticsReader;

    @GetMapping("/search")
    public ResponseEntity<ProblemSearchResponse> searchProblems(
            @RequestParam(required = false) final String query,
            @RequestParam(required = false) final Long lastBojProblemId
    ) {
        ProblemPage page = problemFinder.searchProblems(query, lastBojProblemId);

        return ResponseEntity.ok(ProblemSearchResponse.from(page));
    }

    @GetMapping("/{bojProblemId}")
    public ResponseEntity<ProblemDetailResponse> findProblemDetail(
            @PathVariable final long bojProblemId
    ) {
        ProblemDetail detail = problemFinder.findProblemDetail(bojProblemId);

        return ResponseEntity.ok(ProblemDetailResponse.from(detail));
    }

    @GetMapping("/{bojProblemId}/solve-time-distribution")
    public ResponseEntity<SolveTimeDistributionResponse> getSolveTimeDistribution(
            @PathVariable final long bojProblemId,
            @RequestParam final int solveTimeSeconds
    ) {
        SolveTimeDistributionResponse response = solvedStatisticsReader.getSolveTimeDistribution(
                bojProblemId, solveTimeSeconds
        );

        return ResponseEntity.ok(response);
    }
}
