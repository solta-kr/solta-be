package kr.solta.application.provided;

public interface AuthTokenCreator {

    String createAuthToken(final String code);
}
