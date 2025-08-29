package kr.solta.application.provided;

import jakarta.validation.Valid;
import kr.solta.application.provided.request.SolvedRegisterRequest;
import kr.solta.domain.Solved;

public interface SolvedRegister {

    Solved register(@Valid SolvedRegisterRequest solvedRegisterRequest);
}
