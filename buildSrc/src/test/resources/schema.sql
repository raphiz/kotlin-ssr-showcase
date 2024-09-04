CREATE TABLE task
(
    id INT NOT NULL PRIMARY KEY,
    name TEXT,
    priority SMALLINT
);

CREATE INDEX IF NOT EXISTS idx_task_name_priority ON task (name, priority)
