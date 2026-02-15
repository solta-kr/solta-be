package kr.solta.application.provided;

import kr.solta.application.provided.request.AuthMember;
import kr.solta.domain.AuthCode;

public interface AuthCodeCreator {

    AuthCode create(final AuthMember authMember);
}
