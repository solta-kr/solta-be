package kr.solta.application.provided;

import kr.solta.application.provided.response.ProblemPage;

public interface ProblemFinder {

    ProblemPage searchProblems(final String query, final Long lastBojProblemId);
}