package kr.solta.application.required;

import java.util.Optional;
import kr.solta.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByKey(String key);
}
