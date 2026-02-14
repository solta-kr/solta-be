package kr.solta.support;

import kr.solta.application.required.SolvedAcRateLimiter;

public class FakeSolvedAcRateLimiter implements SolvedAcRateLimiter {

    @Override
    public void waitForNext() {
    }
}