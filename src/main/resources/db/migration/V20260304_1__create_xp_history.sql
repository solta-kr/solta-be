CREATE TABLE xp_history
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    member_id    BIGINT       NOT NULL,
    solved_id    BIGINT       NOT NULL,
    xp_amount    INT          NOT NULL,
    solve_type   VARCHAR(20)  NOT NULL,
    tier_weight  DECIMAL(4,1) NOT NULL,
    streak_bonus DECIMAL(4,2) NOT NULL DEFAULT 0,
    created_at   DATETIME     NOT NULL,
    updated_at   DATETIME     NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (solved_id) REFERENCES solved (id)
);

-- 기존 solved 데이터 백필 (streak_bonus = 0, 복습 보너스 없음)
INSERT INTO xp_history (member_id, solved_id, xp_amount, solve_type, tier_weight, streak_bonus, created_at, updated_at)
WITH tw AS (SELECT id,
                   CASE tier
                       WHEN 'B5' THEN 0.5 WHEN 'B4' THEN 0.6 WHEN 'B3' THEN 0.7 WHEN 'B2' THEN 0.8 WHEN 'B1' THEN 0.9
                       WHEN 'S5' THEN 1.0 WHEN 'S4' THEN 1.2 WHEN 'S3' THEN 1.4 WHEN 'S2' THEN 1.6 WHEN 'S1' THEN 1.8
                       WHEN 'G5' THEN 2.0 WHEN 'G4' THEN 2.3 WHEN 'G3' THEN 2.6 WHEN 'G2' THEN 3.0 WHEN 'G1' THEN 3.5
                       WHEN 'P5' THEN 4.0 WHEN 'P4' THEN 4.5 WHEN 'P3' THEN 5.0 WHEN 'P2' THEN 5.5 WHEN 'P1' THEN 6.0
                       WHEN 'D5' THEN 6.5 WHEN 'D4' THEN 7.0 WHEN 'D3' THEN 7.5 WHEN 'D2' THEN 8.0 WHEN 'D1' THEN 8.5
                       WHEN 'R5' THEN 9.0 WHEN 'R4' THEN 9.5 WHEN 'R3' THEN 10.0 WHEN 'R2' THEN 10.5 WHEN 'R1' THEN 11.0
                       ELSE 0.0
                       END AS xp_weight
            FROM problem)
SELECT s.member_id,
       s.id,
       CASE
           WHEN s.solve_type = 'SOLUTION' OR s.solve_time_seconds IS NULL OR s.solve_time_seconds = 0
               THEN ROUND(tw.xp_weight * 15)
           ELSE ROUND(
                   CASE
                       WHEN s.solve_time_seconds / 60.0 <= 60
                           THEN s.solve_time_seconds / 60.0
                       ELSE 60.0 + (LEAST(s.solve_time_seconds / 60.0, 240.0) - 60.0) * 0.5
                       END * tw.xp_weight * 1.5)
           END,
       s.solve_type,
       tw.xp_weight,
       0.00,
       s.solved_time,
       s.solved_time
FROM solved s
         JOIN tw ON tw.id = s.problem_id
WHERE tw.xp_weight > 0.0;

-- member total_xp 업데이트
UPDATE member m
    JOIN (SELECT member_id, SUM(xp_amount) AS total FROM xp_history GROUP BY member_id) x
    ON x.member_id = m.id
SET m.total_xp = x.total;

-- level 계산
UPDATE member
SET level = CASE
                WHEN total_xp >= 270000 THEN 100
                WHEN total_xp >= 190000 THEN 96 + (total_xp - 190000) DIV 20000
                WHEN total_xp >= 150000 THEN 91 + (total_xp - 150000) DIV 8000
                WHEN total_xp >= 45000  THEN 61 + (total_xp - 45000) DIV 3500
                WHEN total_xp >= 9000   THEN 31 + (total_xp - 9000) DIV 1200
                WHEN total_xp >= 1500   THEN 11 + (total_xp - 1500) DIV 375
                ELSE                         1 + total_xp DIV 150
    END;
