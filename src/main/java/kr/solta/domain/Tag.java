package kr.solta.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseEntity {

    @Id
    private Integer id;

    @Column(name = "tag_key", nullable = false, unique = true)
    private String key;

    @Column(nullable = false)
    private String korName;
}
