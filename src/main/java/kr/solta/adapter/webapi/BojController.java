package kr.solta.adapter.webapi;

import jakarta.validation.Valid;
import kr.solta.adapter.webapi.resolver.Auth;
import kr.solta.adapter.webapi.response.AuthCodeResponse;
import kr.solta.application.provided.AuthCodeCreator;
import kr.solta.application.provided.BojIdVerifier;
import kr.solta.application.provided.request.AuthMember;
import kr.solta.application.provided.request.BojVerifyRequest;
import kr.solta.domain.AuthCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boj")
@RequiredArgsConstructor
public class BojController {

    private final AuthCodeCreator authCodeCreator;
    private final BojIdVerifier bojIdVerifier;

    @PostMapping("/auth-code")
    public ResponseEntity<AuthCodeResponse> createAuthCode(@Auth final AuthMember authMember) {
        AuthCode authCode = authCodeCreator.create(authMember);

        return ResponseEntity.ok(new AuthCodeResponse(authCode.getCode()));
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(
            @Auth final AuthMember authMember,
            @Valid @RequestBody final BojVerifyRequest request
    ) {
        bojIdVerifier.verify(authMember, request.shareUrl());

        return ResponseEntity.ok().build();
    }
}
