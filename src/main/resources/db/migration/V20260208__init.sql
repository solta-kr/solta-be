CREATE TABLE member
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    github_id  BIGINT       NOT NULL,
    boj_id     VARCHAR(255),
    avatar_url VARCHAR(255) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT uk_member_name UNIQUE (name),
    CONSTRAINT uk_member_github_id UNIQUE (github_id),
    CONSTRAINT uk_member_boj_id UNIQUE (boj_id)
);

CREATE TABLE problem
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    boj_problem_id BIGINT       NOT NULL,
    tier           VARCHAR(255) NOT NULL,
    level          INT          NOT NULL,
    created_at     DATETIME(6),
    updated_at     DATETIME(6),
    CONSTRAINT uk_problem_boj_problem_id UNIQUE (boj_problem_id)
);

CREATE TABLE tag
(
    id         INT PRIMARY KEY,
    tag_key    VARCHAR(255) NOT NULL,
    kor_name   VARCHAR(255) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT uk_tag_key UNIQUE (tag_key)
);

CREATE TABLE problem_tag
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    problem_id BIGINT NOT NULL,
    tag_id     INT    NOT NULL,
    CONSTRAINT fk_problem_tag_problem FOREIGN KEY (problem_id) REFERENCES problem (id),
    CONSTRAINT fk_problem_tag_tag FOREIGN KEY (tag_id) REFERENCES tag (id)
);

CREATE TABLE solved
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    solve_time_seconds INT,
    solve_type         VARCHAR(255) NOT NULL,
    member_id          BIGINT       NOT NULL,
    problem_id         BIGINT       NOT NULL,
    solved_time        DATETIME(6),
    created_at         DATETIME(6),
    updated_at         DATETIME(6),
    CONSTRAINT fk_solved_member FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT fk_solved_problem FOREIGN KEY (problem_id) REFERENCES problem (id)
);