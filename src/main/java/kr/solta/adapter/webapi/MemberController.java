package kr.solta.adapter.webapi;

import kr.solta.adapter.webapi.resolver.Auth;
import kr.solta.adapter.webapi.response.MemberResponse;
import kr.solta.application.provided.MemberReader;
import kr.solta.application.provided.request.AuthMember;
import kr.solta.application.provided.response.MemberProfileResponse;
import kr.solta.application.provided.response.SolveTimeTrendsResponse;
import kr.solta.domain.Member;
import kr.solta.domain.SolvedPeriod;
import kr.solta.domain.TierGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberReader memberReader;

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo(@Auth final AuthMember authMember) {
        Member member = memberReader.getMemberById(authMember.memberId());

        return ResponseEntity.ok(MemberResponse.from(member));
    }

    @GetMapping("/profile")
    public ResponseEntity<MemberProfileResponse> getMyInfo(@RequestParam final String name) {
        MemberProfileResponse memberProfile = memberReader.getMemberProfile(name);

        return ResponseEntity.ok(memberProfile);
    }

    @GetMapping("/{name}/solve-time-trends")
    public ResponseEntity<SolveTimeTrendsResponse> getSolveTimeTrends(
            @PathVariable final String name,
            @RequestParam("period") final SolvedPeriod solvedPeriod,
            @RequestParam final TierGroup tierGroup
    ) {
        SolveTimeTrendsResponse response = memberReader.getSolveTimeTrends(name, solvedPeriod, tierGroup);

        return ResponseEntity.ok(response);
    }
}
