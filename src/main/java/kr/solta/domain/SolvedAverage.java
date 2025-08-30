package kr.solta.domain;

public record SolvedAverage(
        Problem problem,
        Double averageSolvedSeconds
) {
    public Long problemId() {
        return problem.getId();
    }
}
