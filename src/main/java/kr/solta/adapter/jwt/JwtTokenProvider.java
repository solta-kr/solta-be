package kr.solta.adapter.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.util.Date;
import kr.solta.application.required.TokenProvider;
import kr.solta.application.required.dto.TokenPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProvider {

    private final JwtProperties jwtProperties;

    @Override
    public String issue(final TokenPayload tokenPayload) {
        Instant currentInstant = Instant.now();
        Instant expireInstant = currentInstant.plus(jwtProperties.getExpireDuration());

        Date currentDate = Date.from(currentInstant);
        Date expireDate = Date.from(expireInstant);

        return Jwts.builder()
                .issuedAt(currentDate)
                .expiration(expireDate)
                .claims()
                .add("memberId", tokenPayload.memberId())
                .add("name", tokenPayload.name())
                .and()
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    @Override
    public TokenPayload extractPayload(final String token) {
        Claims claims = extractClaims(token);

        return new TokenPayload(
                claims.get("memberId", Long.class),
                claims.get("name", String.class)
        );
    }

    private Claims extractClaims(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(jwtProperties.getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("만료된 토큰입니다.", e);
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 형식의 토큰입니다.", e);
        }
    }
}
