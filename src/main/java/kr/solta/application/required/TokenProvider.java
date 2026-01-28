package kr.solta.application.required;

import kr.solta.application.required.dto.TokenPayload;

public interface TokenProvider {

    String issue(final TokenPayload tokenPayload);

    TokenPayload extractPayload(final String token);
}
