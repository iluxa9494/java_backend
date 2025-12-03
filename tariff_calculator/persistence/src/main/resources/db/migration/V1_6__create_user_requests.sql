CREATE TABLE user_requests (
    id UUID PRIMARY KEY,
    ip_address VARCHAR,
    user_agent VARCHAR,
    created_at TIMESTAMP DEFAULT NOW()
);