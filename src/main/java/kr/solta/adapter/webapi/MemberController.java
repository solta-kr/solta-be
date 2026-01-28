package kr.solta.adapter.webapi;

import kr.solta.adapter.webapi.resolver.Auth;
import kr.solta.adapter.webapi.resolver.AuthMember;
import kr.solta.adapter.webapi.response.MemberResponse;
import kr.solta.application.MemberService;
import kr.solta.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo(@Auth final AuthMember authMember) {
        Member member = memberService.getMemberById(authMember.memberId());
        return ResponseEntity.ok(MemberResponse.from(member));
    }
}
