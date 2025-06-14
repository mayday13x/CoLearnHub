-- Create Study_Sessions table
CREATE TABLE Study_Sessions (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    description VARCHAR,
    creator_id UUID NOT NULL REFERENCES Users(id),
    group_id BIGINT REFERENCES Groups(id),
    visibility BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    date DATE NOT NULL,
    start_time TIMESTAMPTZ NOT NULL,
    duration BIGINT NOT NULL,
    tag_id BIGINT REFERENCES Tags(id)
);

-- Add indexes for better query performance
CREATE INDEX idx_study_sessions_creator_id ON Study_Sessions(creator_id);
CREATE INDEX idx_study_sessions_group_id ON Study_Sessions(group_id);
CREATE INDEX idx_study_sessions_tag_id ON Study_Sessions(tag_id);
CREATE INDEX idx_study_sessions_date ON Study_Sessions(date); 