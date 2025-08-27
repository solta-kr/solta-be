package kr.solta.adapter.webapi;

import jakarta.validation.Valid;
import kr.solta.adapter.webapi.response.SolvedRegisterResponse;
import kr.solta.application.provided.SolvedRegister;
import kr.solta.application.provided.SolvedRegisterRequest;
import kr.solta.domain.Solved;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/solveds")
@RestController
@RequiredArgsConstructor
public class SolvedController {

    private final SolvedRegister solvedRegister;

    @PostMapping
    public ResponseEntity<SolvedRegisterResponse> register(
            @Valid @RequestBody SolvedRegisterRequest solvedRegisterRequest
    ) {
        Solved solved = solvedRegister.register(solvedRegisterRequest);

        return ResponseEntity.ok(SolvedRegisterResponse.from(solved));
    }
}
