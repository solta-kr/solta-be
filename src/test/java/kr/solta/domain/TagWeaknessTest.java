package kr.solta.domain;

import static kr.solta.support.TestFixtures.createTag;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;

class TagWeaknessTest {

    private final Tag dpTag = createTag(1, "dp", "다이나믹 프로그래밍");

    @Test
    void 자력풀이가_없으면_약점점수가_HIGH이다() {
        // selfCount=0, totalCount=5 → solutionRateScore=1.0
        // avgSolveSeconds=null → timeScore=0.5(중립)
        // weaknessScore = 1.0*0.6 + 0.5*0.4 = 0.8 → HIGH
        TagWeakness weakness = new TagWeakness(dpTag, 5L, 0L, null, 1800.0);

        assertSoftly(softly -> {
            softly.assertThat(weakness.getSelfSolveRate()).isEqualTo(0);
            softly.assertThat(weakness.getWeaknessScore()).isGreaterThanOrEqualTo(0.55);
            softly.assertThat(weakness.getWeaknessLevel()).isEqualTo(WeaknessLevel.HIGH);
        });
    }

    @Test
    void 모두_자력으로_풀고_평균시간이_짧으면_약점점수가_LOW이다() {
        // selfCount=10, totalCount=10 → solutionRateScore=0
        // timeRatio=900/1800=0.5 → timeScore=0
        // weaknessScore = 0 → LOW
        TagWeakness weakness = new TagWeakness(dpTag, 10L, 10L, 900.0, 1800.0);

        assertThat(weakness.getWeaknessLevel()).isEqualTo(WeaknessLevel.LOW);
    }

    @Test
    void 약점등급_MEDIUM_판정() {
        // selfCount=5, totalCount=10 → solutionRateScore=0.5
        // avgSolveSeconds=null → timeScore=0.5
        // weaknessScore = 0.5*0.6 + 0.5*0.4 = 0.5 → MEDIUM
        TagWeakness weakness = new TagWeakness(dpTag, 10L, 5L, null, null);

        assertThat(weakness.getWeaknessLevel()).isEqualTo(WeaknessLevel.MEDIUM);
    }

    @Test
    void 상대_풀이시간이_길수록_약점점수가_높다() {
        // 둘 다 selfCount=totalCount (solutionRateScore=0), 풀이시간만 다름
        TagWeakness fast = new TagWeakness(dpTag, 10L, 10L, 900.0, 1800.0);   // timeRatio=0.5 → timeScore=0
        TagWeakness slow = new TagWeakness(dpTag, 10L, 10L, 3600.0, 1800.0);  // timeRatio=2.0 → timeScore=1.0

        assertThat(slow.getWeaknessScore()).isGreaterThan(fast.getWeaknessScore());
    }

    @Test
    void avgSolveSeconds가_null이면_timeRatio는_null이다() {
        TagWeakness weakness = new TagWeakness(dpTag, 5L, 3L, null, 1800.0);

        assertThat(weakness.getTimeRatio()).isNull();
    }

    @Test
    void userOverallAvg가_0이면_timeRatio는_null이다() {
        TagWeakness weakness = new TagWeakness(dpTag, 5L, 3L, 3600.0, 0.0);

        assertThat(weakness.getTimeRatio()).isNull();
    }

    @Test
    void userOverallAvg가_null이면_timeRatio는_null이다() {
        TagWeakness weakness = new TagWeakness(dpTag, 5L, 3L, 3600.0, null);

        assertThat(weakness.getTimeRatio()).isNull();
    }

    @Test
    void selfSolveRate_계산이_올바르다() {
        TagWeakness weakness = new TagWeakness(dpTag, 10L, 4L, null, null);

        assertThat(weakness.getSelfSolveRate()).isEqualTo(40);
    }

    @Test
    void 신뢰도는_문제수가_20개_이상이면_1이다() {
        TagWeakness weakness = new TagWeakness(dpTag, 20L, 10L, null, null);

        assertThat(weakness.getConfidence()).isEqualTo(1.0);
    }

    @Test
    void 신뢰도는_문제수가_적을수록_1보다_작다() {
        TagWeakness weakness = new TagWeakness(dpTag, 3L, 2L, null, null);

        assertThat(weakness.getConfidence()).isLessThan(1.0);
    }

    @Test
    void finalScore는_약점점수와_신뢰도의_곱이다() {
        // totalCount=20 → confidence=1.0
        TagWeakness weakness = new TagWeakness(dpTag, 20L, 0L, null, null);

        assertThat(weakness.getFinalScore()).isEqualTo(weakness.getWeaknessScore() * weakness.getConfidence());
    }

    @Test
    void tag_정보를_반환한다() {
        TagWeakness weakness = new TagWeakness(dpTag, 5L, 3L, null, null);

        assertSoftly(softly -> {
            softly.assertThat(weakness.getTag().getKey()).isEqualTo("dp");
            softly.assertThat(weakness.getTag().getKorName()).isEqualTo("다이나믹 프로그래밍");
        });
    }

    @Test
    void timeRatio는_내_평균_대비_상대_풀이시간이다() {
        // avgSolveSeconds=3600, userOverallAvg=1800 → timeRatio=2.0
        TagWeakness weakness = new TagWeakness(dpTag, 5L, 3L, 3600.0, 1800.0);

        assertThat(weakness.getTimeRatio()).isEqualTo(2.0);
    }
}
