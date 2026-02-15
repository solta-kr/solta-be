CREATE TABLE auth_code
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT       NOT NULL,
    code       VARCHAR(6)   NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT uk_auth_code_code UNIQUE (code),
    CONSTRAINT fk_auth_code_member FOREIGN KEY (member_id) REFERENCES member (id)
);