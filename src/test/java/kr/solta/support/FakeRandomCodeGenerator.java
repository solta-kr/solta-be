package kr.solta.support;

import kr.solta.application.required.RandomCodeGenerator;

public class FakeRandomCodeGenerator implements RandomCodeGenerator {

    public static final String FAKE_CODE = "Ab3xYz";

    @Override
    public String generate(final int length) {
        return FAKE_CODE;
    }
}