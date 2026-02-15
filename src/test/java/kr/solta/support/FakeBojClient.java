package kr.solta.support;

import kr.solta.application.required.BojClient;
import kr.solta.application.required.dto.BojSubmissionResponse;

public class FakeBojClient implements BojClient {

    public static final String FAKE_BOJ_ID = "fakeBojUser";
    public static final String FAKE_SOURCE_CODE = FakeRandomCodeGenerator.FAKE_CODE;

    @Override
    public BojSubmissionResponse getSubmission(final String shareUrl) {
        return new BojSubmissionResponse(FAKE_BOJ_ID, FAKE_SOURCE_CODE);
    }
}
