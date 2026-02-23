package kr.solta.application.provided;

import java.time.LocalDate;
import kr.solta.application.provided.response.ActivityHeatmapResponse;

public interface ActivityReader {

    ActivityHeatmapResponse getActivityHeatmap(final String name, final LocalDate startDate, final LocalDate endDate);
}
