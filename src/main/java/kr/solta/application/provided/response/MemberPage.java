package kr.solta.application.provided.response;

import java.util.List;
import kr.solta.domain.Member;

public record MemberPage(
        List<Member> members,
        boolean hasNext
) {
}
