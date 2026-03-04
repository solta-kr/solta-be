package kr.solta.application.provided;

import kr.solta.application.provided.response.XpHistoryResponse;
import kr.solta.application.provided.response.XpSummaryResponse;
import kr.solta.domain.SolvedPeriod;

public interface XpReader {

    XpSummaryResponse getXpSummary(final String username);

    XpHistoryResponse getXpHistory(final String username, final SolvedPeriod period);
}
