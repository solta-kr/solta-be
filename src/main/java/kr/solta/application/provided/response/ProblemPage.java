package kr.solta.application.provided.response;

import java.util.List;

public record ProblemPage(
        List<ProblemWithTags> problems,
        boolean hasNext
) {
}