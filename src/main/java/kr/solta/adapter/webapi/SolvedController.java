package kr.solta.adapter.webapi;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.solta.adapter.webapi.response.RecentSolvedResponse;
import kr.solta.adapter.webapi.response.SolvedRegisterResponse;
import kr.solta.application.provided.SolvedFinder;
import kr.solta.application.provided.SolvedRegister;
import kr.solta.application.provided.request.SolvedRegisterRequest;
import kr.solta.application.provided.response.SolvedWithTags;
import kr.solta.domain.Solved;
import kr.solta.domain.Tier;
import kr.solta.domain.TierAverage;
import kr.solta.domain.TierGroup;
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
    public ResponseEntity<List<RecentSolvedResponse>> findSolvedWithAverages(
            @PathVariable String bojId
    ) {
        List<SolvedWithTags> solvedWithTags = solvedFinder.findSolvedWithTags(bojId);

        List<RecentSolvedResponse> recentSolvedResponses = solvedWithTags.stream()
                .map(RecentSolvedResponse::from)
                .toList();

        return ResponseEntity.ok(recentSolvedResponses);
    }

    @GetMapping("/members/{bojId}/solveds/tier-group/average-time")
    public ResponseEntity<List<TierGroupAverage>> findTierGroupAverageTime(
            @PathVariable String bojId
    ) {
        List<TierGroupAverage> tierGroupAverages = solvedFinder.findTierGroupAverages(bojId);

        return ResponseEntity.ok(tierGroupAverages);
    }

    @GetMapping("/members/{bojId}/solveds/tier/average-time")
    public ResponseEntity<Map<TierGroup, List<TierAverage>>> findTierAverageTime(
            @PathVariable String bojId
    ) {
        Map<Tier, TierAverage> tierAverages = solvedFinder.findTierAverages(bojId)
                .stream()
                .collect(Collectors.toMap(
                        TierAverage::tier,
                        t -> t
                ));

        Map<TierGroup, List<TierAverage>> responses = Arrays.stream(TierGroup.values())
                .collect(Collectors.toMap(
                        tg -> tg,
                        tg -> tg.getTiers().stream()
                                .map(t -> tierAverages.getOrDefault(t, new TierAverage(t, null, 0)))
                                .toList()
                ));

        return ResponseEntity.ok(responses);
    }
}
