package kr.solta.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Map;
import kr.solta.domain.SolveTimeDistribution.Bucket;
import org.junit.jupiter.api.Test;

class SolveTimeDistributionTest {

    @Test
    void 최대_풀이_시간이_작으면_버킷_크기는_최소_300초이다() {
        //given & when
        SolveTimeDistribution distribution = new SolveTimeDistribution(500);

        //then
        assertThat(distribution.getBucketSize()).isEqualTo(300);
    }

    @Test
    void 최대_풀이_시간이_크면_버킷_크기가_300초_단위로_올림된다() {
        //given & when - maxTime 7200초 → rawBucket 360 → 600으로 올림
        SolveTimeDistribution distribution = new SolveTimeDistribution(7200);

        //then
        assertThat(distribution.getBucketSize()).isEqualTo(600);
    }

    @Test
    void 버킷_개수가_최대_20개를_넘지_않는다() {
        //given & when
        SolveTimeDistribution distribution = new SolveTimeDistribution(18000);

        //then
        distribution.fillBuckets(Map.of());
        assertThat(distribution.getBuckets().size()).isLessThanOrEqualTo(20);
    }

    @Test
    void 빈_구간은_count_0으로_채워진다() {
        //given
        SolveTimeDistribution distribution = new SolveTimeDistribution(900);

        //when - bucket 0과 2에만 데이터, bucket 1은 비어있음
        distribution.fillBuckets(Map.of(0L, 3L, 2L, 1L));

        //then
        assertSoftly(softly -> {
            softly.assertThat(distribution.getBuckets()).hasSize(3);
            softly.assertThat(distribution.getBuckets().get(0)).isEqualTo(new Bucket(0, 300, 3));
            softly.assertThat(distribution.getBuckets().get(1)).isEqualTo(new Bucket(300, 600, 0));
            softly.assertThat(distribution.getBuckets().get(2)).isEqualTo(new Bucket(600, 900, 1));
        });
    }

    @Test
    void empty로_생성하면_빈_분포를_반환한다() {
        //given & when
        SolveTimeDistribution distribution = SolveTimeDistribution.empty();

        //then
        assertSoftly(softly -> {
            softly.assertThat(distribution.getBucketSize()).isEqualTo(300);
            softly.assertThat(distribution.getBuckets()).isEmpty();
        });
    }

    @Test
    void empty에_fillBuckets를_호출해도_빈_분포를_유지한다() {
        //given
        SolveTimeDistribution distribution = SolveTimeDistribution.empty();

        //when
        distribution.fillBuckets(Map.of(0L, 5L));

        //then
        assertThat(distribution.getBuckets()).isEmpty();
    }

    @Test
    void 상위_퍼센트를_정확히_계산한다() {
        //given
        SolveTimeDistribution distribution = new SolveTimeDistribution(1000);

        //when - 10명 중 5명이 느림 → 상위 50%
        double topPercent = distribution.calculateTopPercent(10, 5);

        //then
        assertThat(topPercent).isEqualTo(50.0);
    }

    @Test
    void 모든_사람보다_빠르면_상위_0퍼센트이다() {
        //given
        SolveTimeDistribution distribution = new SolveTimeDistribution(1000);

        //when - 10명 모두 느림
        double topPercent = distribution.calculateTopPercent(10, 10);

        //then
        assertThat(topPercent).isEqualTo(0.0);
    }

    @Test
    void 모든_사람보다_느리면_상위_100퍼센트이다() {
        //given
        SolveTimeDistribution distribution = new SolveTimeDistribution(1000);

        //when - 아무도 느리지 않음
        double topPercent = distribution.calculateTopPercent(10, 0);

        //then
        assertThat(topPercent).isEqualTo(100.0);
    }

    @Test
    void totalCount가_0이면_상위_0퍼센트를_반환한다() {
        //given
        SolveTimeDistribution distribution = SolveTimeDistribution.empty();

        //when
        double topPercent = distribution.calculateTopPercent(0, 0);

        //then
        assertThat(topPercent).isEqualTo(0.0);
    }

    @Test
    void 소수점_첫째_자리까지_반올림한다() {
        //given
        SolveTimeDistribution distribution = new SolveTimeDistribution(1000);

        //when - 3명 중 1명 느림 → 상위 66.666...% → 66.7%
        double topPercent = distribution.calculateTopPercent(3, 1);

        //then
        assertThat(topPercent).isEqualTo(66.7);
    }
}
