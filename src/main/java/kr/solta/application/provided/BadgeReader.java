package kr.solta.application.provided;

import kr.solta.application.provided.response.BadgeStatsResponse;

public interface BadgeReader {

    String generateBadgeSvg(String username);

    BadgeStatsResponse getBadgeStats(String username);
}
