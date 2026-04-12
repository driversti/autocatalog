CREATE TABLE image_sources (
    id          BIGSERIAL PRIMARY KEY,
    image_url   TEXT NOT NULL UNIQUE,
    source_url  TEXT,
    author      VARCHAR(255),
    license     VARCHAR(100) NOT NULL,
    license_url TEXT,
    copyright   VARCHAR(500),
    usage_terms TEXT,
    obtained_at TIMESTAMP NOT NULL DEFAULT NOW()
);
