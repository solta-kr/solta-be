package kr.solta.application.provided.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FeedItemResponse(
        String memberName,
        String memberAvatarUrl,
        long problemBojId,
        String problemTitle,
        String problemTier,
        String solveType,
        Integer solveTimeSeconds,
        LocalDateTime solvedAt
) {}
