package kr.solta.application.provided.request;

import java.util.Arrays;

public enum TagKey {
    MATH("math", "수학"),
    IMPLEMENTATION("implementation", "구현"),
    GREEDY("greedy", "그리디 알고리즘"),
    STRING("string", "문자열"),
    DATA_STRUCTURES("data_structures", "자료 구조"),
    GRAPHS("graphs", "그래프 이론"),
    DP("dp", "다이나믹 프로그래밍"),
    GEOMETRY("geometry", "기하학"),
    BINARY_SEARCH("binary_search", "이분탐색");

    private final String key;
    private final String korName;

    TagKey(final String key, final String korName) {
        this.key = key;
        this.korName = korName;
    }

    public String getKey() {
        return key;
    }

    public String getKorName() {
        return korName;
    }

    public static TagKey fromKey(final String key) {
        return Arrays.stream(values())
                .filter(tagKey -> tagKey.key.equals(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid tag key: " + key));
    }
}
