package kr.solta.application.provided;

import jakarta.validation.Valid;
import kr.solta.application.provided.request.AuthMember;
import kr.solta.application.provided.request.SolvedRegisterRequest;
import kr.solta.domain.Solved;

public interface SolvedRegister {

    Solved register(final AuthMember authMember, @Valid final SolvedRegisterRequest solvedRegisterRequest);
}
