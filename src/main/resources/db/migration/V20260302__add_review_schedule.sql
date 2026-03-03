ALTER TABLE member
    ADD COLUMN default_review_interval INT NULL;

CREATE TABLE review_schedule
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id        BIGINT       NOT NULL,
    problem_id       BIGINT       NOT NULL,
    origin_solved_id BIGINT       NOT NULL,
    scheduled_date   DATE         NOT NULL,
    round            INT          NOT NULL DEFAULT 1,
    interval_days    INT          NOT NULL DEFAULT 3,
    status           VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at       DATETIME(6),
    updated_at       DATETIME(6),
    CONSTRAINT fk_review_schedule_member FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT fk_review_schedule_problem FOREIGN KEY (problem_id) REFERENCES problem (id),
    CONSTRAINT fk_review_schedule_origin_solved FOREIGN KEY (origin_solved_id) REFERENCES solved (id)
);

CREATE INDEX idx_review_schedule_member_status ON review_schedule (member_id, status);
CREATE INDEX idx_review_schedule_status_date ON review_schedule (status, scheduled_date);
