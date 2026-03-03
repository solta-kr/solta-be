package kr.solta.application.provided;

import kr.solta.application.provided.response.ReviewHistoryResponse;
import kr.solta.application.provided.response.ReviewListResponse;

public interface ReviewScheduleFinder {

    ReviewListResponse findReviews(String name);

    ReviewHistoryResponse findCompletedReviews(String name);
}
