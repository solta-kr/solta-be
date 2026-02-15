package kr.solta.adapter.boj;

import java.io.IOException;
import kr.solta.application.required.BojClient;
import kr.solta.application.required.dto.BojSubmissionResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class BojJsoupClient implements BojClient {

    private static final String BOJ_SHORT_URL_PREFIX = "http://boj.kr/";
    private static final String BOJ_SHARE_URL_PREFIX = "https://www.acmicpc.net/source/share/";
    private static final int CONNECT_TIMEOUT_MILLIS = 5_000;
    private static final int READ_TIMEOUT_MILLIS = 10_000;

    @Override
    public BojSubmissionResponse getSubmission(final String shareUrl) {
        String resolvedUrl = resolveUrl(shareUrl);

        try {
            Document document = Jsoup.connect(resolvedUrl)
                    .timeout(CONNECT_TIMEOUT_MILLIS + READ_TIMEOUT_MILLIS)
                    .get();

            String bojId = extractBojId(document);
            String sourceCode = extractSourceCode(document);

            return new BojSubmissionResponse(bojId, sourceCode);
        } catch (IOException e) {
            throw new IllegalArgumentException("BOJ 소스 공유 페이지를 불러올 수 없습니다. URL: " + shareUrl, e);
        }
    }

    private String resolveUrl(final String shareUrl) {
        if (shareUrl.startsWith(BOJ_SHORT_URL_PREFIX)) {
            String hash = shareUrl.substring(BOJ_SHORT_URL_PREFIX.length());
            return BOJ_SHARE_URL_PREFIX + hash;
        }
        return shareUrl;
    }

    private String extractBojId(final Document document) {
        Element userLink = document.selectFirst(".breadcrumb a[href^=/user/]");
        if (userLink == null) {
            throw new IllegalArgumentException("BOJ 사용자 정보를 찾을 수 없습니다.");
        }
        return userLink.text();
    }

    private String extractSourceCode(final Document document) {
        Element textarea = document.selectFirst("textarea.codemirror-textarea");
        if (textarea == null) {
            throw new IllegalArgumentException("소스코드를 찾을 수 없습니다.");
        }
        return textarea.text();
    }
}
