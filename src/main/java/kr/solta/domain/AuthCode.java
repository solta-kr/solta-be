package kr.solta.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthCode extends BaseEntity {

    public static final int CODE_LENGTH = 6;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @Column(unique = true)
    private String code;

    public AuthCode(final Member member, final String code) {
        validateCodeLength(code);
        
        this.member = member;
        this.code = code;
    }

    public void validateCode(final Member member, final String code) {
        if(!this.member.equals(member)) {
            throw new IllegalArgumentException("인증하려는 사용자가 아닙니다.");
        }

        if(!this.code.equals(code)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }
    }

    private void validateCodeLength(final String code) {
        if (code == null || code.length() != CODE_LENGTH) {
            throw new IllegalArgumentException("인증 코드는 " + CODE_LENGTH + "자리여야 합니다.");
        }
    }
}
