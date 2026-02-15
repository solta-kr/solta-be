package kr.solta.application.required;

import kr.solta.application.required.dto.BojSubmissionResponse;

public interface BojClient {

    BojSubmissionResponse getSubmission(String shareUrl);
}
