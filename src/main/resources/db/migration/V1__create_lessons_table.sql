CREATE TABLE lessons (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  course_id BIGINT NOT NULL,
  sequence_number BIGINT NOT NULL,
  description VARCHAR(1000),
  content TEXT
);