package kr.solta.domain;

public record SolvedAverage(
        Problem problem,
        double averageSolvedSeconds
) {
    public Long problemId() {
        return problem.getId();
    }
}
