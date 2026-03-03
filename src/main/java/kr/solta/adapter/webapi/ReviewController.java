package kr.solta.adapter.webapi;

import jakarta.validation.Valid;
import kr.solta.adapter.webapi.request.RescheduleRequest;
import kr.solta.adapter.webapi.request.UpdateReviewSettingRequest;
import kr.solta.adapter.webapi.resolver.Auth;
import kr.solta.application.provided.ReviewScheduleFinder;
import kr.solta.application.provided.ReviewScheduleUpdater;
import kr.solta.application.provided.request.AuthMember;
import kr.solta.application.provided.response.ReviewHistoryResponse;
import kr.solta.application.provided.response.ReviewListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewScheduleFinder reviewScheduleFinder;
    private final ReviewScheduleUpdater reviewScheduleUpdater;

    @GetMapping("/reviews")
    public ResponseEntity<ReviewListResponse> getReviews(@RequestParam final String name) {
        return ResponseEntity.ok(reviewScheduleFinder.findReviews(name));
    }

    @GetMapping("/reviews/completed")
    public ResponseEntity<ReviewHistoryResponse> getCompletedReviews(@RequestParam final String name) {
        return ResponseEntity.ok(reviewScheduleFinder.findCompletedReviews(name));
    }

    @PostMapping("/reviews/{id}/skip")
    public ResponseEntity<Void> skip(
            @PathVariable final Long id,
            @Auth final AuthMember authMember
    ) {
        reviewScheduleUpdater.skip(authMember, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reviews/{id}/reschedule")
    public ResponseEntity<Void> reschedule(
            @PathVariable final Long id,
            @Valid @RequestBody final RescheduleRequest request,
            @Auth final AuthMember authMember
    ) {
        reviewScheduleUpdater.reschedule(authMember, id, request.interval());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/members/me/review-setting")
    public ResponseEntity<Void> updateReviewSetting(
            @Valid @RequestBody final UpdateReviewSettingRequest request,
            @Auth final AuthMember authMember
    ) {
        reviewScheduleUpdater.updateDefaultReviewInterval(authMember, request.defaultReviewInterval());
        return ResponseEntity.noContent().build();
    }
}
