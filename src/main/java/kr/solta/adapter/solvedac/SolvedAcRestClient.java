package kr.solta.adapter.solvedac;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kr.solta.application.required.SolvedAcClient;
import kr.solta.application.required.dto.SolvedAcProblemResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class SolvedAcRestClient implements SolvedAcClient {

    private static final String LOOKUP_URL = "https://solved.ac/api/v3/problem/lookup";

    private final RestClient restClient;

    public SolvedAcRestClient(final RestClient.Builder restClientBuilder) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        this.restClient = restClientBuilder
                .requestFactory(requestFactory)
                .defaultStatusHandler(
                        HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            InputStream bodyStream = response.getBody();
                            String body = new String(bodyStream.readAllBytes(), StandardCharsets.UTF_8);
                            log.warn("[solved.ac] 클라이언트 오류: {}", body);
                            throw new IllegalArgumentException("[solved.ac] 클라이언트 오류: " + body);
                        }
                )
                .defaultStatusHandler(
                        HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            InputStream bodyStream = response.getBody();
                            String body = new String(bodyStream.readAllBytes(), StandardCharsets.UTF_8);
                            log.error("[solved.ac] 서버 오류: {}", body);
                            throw new RuntimeException("[solved.ac] 서버 오류: " + body);
                        }
                )
                .build();
    }

    @Override
    public List<SolvedAcProblemResponse> lookupProblems(final List<Long> problemIds) {
        if (problemIds.isEmpty()) {
            return Collections.emptyList();
        }

        String ids = problemIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        List<SolvedAcProblemResponse> result = restClient.get()
                .uri(LOOKUP_URL + "?problemIds={ids}", ids)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        return result != null ? result : Collections.emptyList();
    }
}
