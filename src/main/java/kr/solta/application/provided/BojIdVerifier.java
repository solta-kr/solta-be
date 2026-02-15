package kr.solta.application.provided;

import kr.solta.application.provided.request.AuthMember;

public interface BojIdVerifier {

    void verify(AuthMember authMember, String shareUrl);
}
