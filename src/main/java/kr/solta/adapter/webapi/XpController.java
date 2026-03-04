package kr.solta.adapter.webapi;

import kr.solta.application.provided.XpReader;
import kr.solta.application.provided.response.XpHistoryResponse;
import kr.solta.application.provided.response.XpSummaryResponse;
import kr.solta.domain.SolvedPeriod;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class XpController {

    private final XpReader xpReader;

    @GetMapping("/members/{username}/xp")
    public ResponseEntity<XpSummaryResponse> getXpSummary(@PathVariable final String username) {
        return ResponseEntity.ok(xpReader.getXpSummary(username));
    }

    @GetMapping("/members/{username}/xp/history")
    public ResponseEntity<XpHistoryResponse> getXpHistory(
            @PathVariable final String username,
            @RequestParam(defaultValue = "WEEK") final SolvedPeriod period
    ) {
        return ResponseEntity.ok(xpReader.getXpHistory(username, period));
    }
}
