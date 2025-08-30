package kr.solta.adapter.webapi;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.solta.adapter.webapi.response.SolvedRegisterResponse;
import kr.solta.adapter.webapi.response.SolvedWithAverageResponse;
import kr.solta.application.provided.SolvedFinder;
import kr.solta.application.provided.SolvedRegister;
import kr.solta.application.provided.request.SolvedRegisterRequest;
import kr.solta.domain.Problem;
import kr.solta.domain.Solved;
import kr.solta.domain.SolvedAverage;
import kr.solta.domain.TierGroupAverage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class SolvedController {

    private final SolvedFinder solvedFinder;
    private final SolvedRegister solvedRegister;

    @PostMapping("/solveds")
    public ResponseEntity<SolvedRegisterResponse> register(
            @Valid @RequestBody SolvedRegisterRequest solvedRegisterRequest
    ) {
        Solved solved = solvedRegister.register(solvedRegisterRequest);

        return ResponseEntity.ok(SolvedRegisterResponse.from(solved));
    }

    @GetMapping("/members/{bojId}/solveds")
    public ResponseEntity<List<SolvedWithAverageResponse>> findSolvedWithAverages(
            @PathVariable String bojId
    ) {
        List<Solved> solveds = solvedFinder.findSolveds(bojId);

        List<Problem> problems = solveds.stream()
                .map(Solved::getProblem)
                .toList();

        List<SolvedAverage> averages = solvedFinder.findSolvedAverages(problems);

        Map<Long, Double> avgMap = averages.stream()
                .collect(Collectors.toMap(
                        SolvedAverage::problemId,
                        SolvedAverage::averageSolvedSeconds)
                );

        List<SolvedWithAverageResponse> responses = solveds.stream()
                .map(solved -> SolvedWithAverageResponse.of(
                        solved,
                        avgMap.getOrDefault(solved.getProblem().getId(), 0.0)
                ))
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/members/{bojId}/solveds/tier-group/average-time")
    public ResponseEntity<List<TierGroupAverage>> findTierGroupAverageTime(
            @PathVariable String bojId
    ) {
        List<TierGroupAverage> tierGroupAverages = solvedFinder.findTierGroupAverages(bojId);

        return ResponseEntity.ok(tierGroupAverages);
    }
}
