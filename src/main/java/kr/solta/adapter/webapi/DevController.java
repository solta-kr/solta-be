package kr.solta.adapter.webapi;

import java.util.Map;
import kr.solta.application.exception.NotFoundEntityException;
import kr.solta.application.required.MemberRepository;
import kr.solta.application.required.TokenProvider;
import kr.solta.application.required.dto.TokenPayload;
import kr.solta.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("local")
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevController {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> getDevToken(
            @RequestParam(defaultValue = "dlwogns3413") final String name
    ) {
        Member member = memberRepository.findByName(name)
                .orElseThrow(() -> new NotFoundEntityException("멤버를 찾을 수 없습니다: " + name));

        String token = tokenProvider.issue(new TokenPayload(member.getId(), member.getName()));
        return ResponseEntity.ok(Map.of("token", token));
    }
}
