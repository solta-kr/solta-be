package kr.solta.adapter.webapi;

import kr.solta.application.provided.BadgeReader;
import kr.solta.application.provided.response.BadgeStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeReader badgeReader;

    @GetMapping("/{username}")
    public ResponseEntity<String> getBadge(@PathVariable final String username) {
        String svg = badgeReader.generateBadgeSvg(username);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/svg+xml"))
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .body(svg);
    }

    @GetMapping("/{username}/stats")
    public ResponseEntity<BadgeStatsResponse> getBadgeStats(@PathVariable final String username) {
        BadgeStatsResponse stats = badgeReader.getBadgeStats(username);
        return ResponseEntity.ok(stats);
    }
}
