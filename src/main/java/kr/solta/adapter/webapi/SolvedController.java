package kr.solta.adapter.webapi;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import kr.solta.adapter.webapi.resolver.Auth;
import kr.solta.adapter.webapi.response.RecentSolvedResponse;
import kr.solta.adapter.webapi.response.SolvedRegisterResponse;
import kr.solta.application.provided.SolvedFinder;
import kr.solta.application.provided.SolvedRegister;
import kr.solta.application.provided.request.AuthMember;
import kr.solta.application.provided.request.SolvedRegisterRequest;
import kr.solta.application.provided.request.SolvedSortType;
import kr.solta.application.provided.request.TagKey;
import kr.solta.application.provided.response.SolvedWithTags;
import kr.solta.domain.Solved;
import kr.solta.domain.TierAverage;
import kr.solta.domain.TierGroup;
import kr.solta.domain.TierGroupAverage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class SolvedController {

    private final SolvedFinder solvedFinder;
    private final SolvedRegister solvedRegister;

    @PostMapping("/solveds")
    public ResponseEntity<SolvedRegisterResponse> register(
            @Valid @RequestBody final SolvedRegisterRequest solvedRegisterRequest,
            @Auth final AuthMember authMember
    ) {
        Solved solved = solvedRegister.register(authMember, solvedRegisterRequest);

        return ResponseEntity.ok(SolvedRegisterResponse.from(solved));
    }

    @GetMapping("/members/solveds/search")
    public ResponseEntity<List<RecentSolvedResponse>> findSolvedWithAverages(@RequestParam final String name) {
        List<SolvedWithTags> solvedWithTags = solvedFinder.findSolvedWithTags(name);

        List<RecentSolvedResponse> recentSolvedResponses = solvedWithTags.stream()
                .map(RecentSolvedResponse::from)
                .toList();

        return ResponseEntity.ok(recentSolvedResponses);
    }

    @GetMapping("/members/solveds/tier-group/average-time/search")
    public ResponseEntity<List<TierGroupAverage>> findTierGroupAverageTime(
            @RequestParam final String name,
            @RequestParam(required = false) final TagKey tagKey
    ) {
        List<TierGroupAverage> tierGroupAverages = solvedFinder.findTierGroupAverages(name, tagKey);

        return ResponseEntity.ok(tierGroupAverages);
    }

    @GetMapping("/members/solveds/tier/average-time/search")
    public ResponseEntity<Map<TierGroup, List<TierAverage>>> findTierAverageTime(@RequestParam final String name) {
        Map<TierGroup, List<TierAverage>> response = solvedFinder.findTierAverages(name);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/members/solveds/retry/search")
    public ResponseEntity<List<RecentSolvedResponse>> findProblemsToRetry(
            @RequestParam final String name,
            @RequestParam(defaultValue = "LATEST") final SolvedSortType sortType
    ) {
        List<SolvedWithTags> solvedWithTags = solvedFinder.findProblemsToRetry(name, sortType);

        List<RecentSolvedResponse> recentSolvedResponses = solvedWithTags.stream()
                .map(RecentSolvedResponse::from)
                .toList();

        return ResponseEntity.ok(recentSolvedResponses);
    }
}
