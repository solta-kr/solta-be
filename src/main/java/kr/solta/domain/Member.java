package kr.solta.domain;

import static java.util.Objects.requireNonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private Long githubId;

    @Column(unique = true)
    private String bojId;

    @Column(nullable = false)
    private String avatarUrl;

    public static Member create(final Long githubId, final String name, final String avatarUrl) {
        Member member = new Member();

        member.name = requireNonNull(name);
        member.githubId = requireNonNull(githubId);
        member.avatarUrl = requireNonNull(avatarUrl);

        return member;
    }

    public void updateBojId(final String bojId) {
        this.bojId = requireNonNull(bojId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
