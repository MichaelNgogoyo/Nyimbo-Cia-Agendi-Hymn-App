CREATE TABLE events (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(140) NOT NULL,
  description TEXT NOT NULL,
  category VARCHAR(40) NOT NULL,
  event_date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  venue VARCHAR(240) NOT NULL,
  organizer VARCHAR(140) NOT NULL,
  image_url VARCHAR(500),
  contact VARCHAR(200),
  latitude DOUBLE PRECISION,
  longitude DOUBLE PRECISION,
  published BOOLEAN NOT NULL DEFAULT FALSE,
  archived BOOLEAN NOT NULL DEFAULT FALSE,
  demo BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  CONSTRAINT ck_event_latitude CHECK (latitude IS NULL OR latitude BETWEEN -90 AND 90),
  CONSTRAINT ck_event_longitude CHECK (longitude IS NULL OR longitude BETWEEN -180 AND 180),
  CONSTRAINT ck_event_coordinate_pair CHECK ((latitude IS NULL) = (longitude IS NULL))
);
CREATE INDEX ix_events_public_date ON events(published, archived, event_date, start_time);

CREATE TABLE announcements (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(160) NOT NULL,
  summary VARCHAR(400) NOT NULL,
  body TEXT NOT NULL,
  image_url VARCHAR(500),
  published BOOLEAN NOT NULL DEFAULT FALSE,
  archived BOOLEAN NOT NULL DEFAULT FALSE,
  demo BOOLEAN NOT NULL DEFAULT FALSE,
  published_at TIMESTAMP,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);
CREATE INDEX ix_announcements_public ON announcements(published, archived, published_at DESC);

CREATE TABLE hymns (
  id BIGSERIAL PRIMARY KEY,
  version INTEGER NOT NULL CHECK (version IN (0, 1)),
  number INTEGER NOT NULL CHECK (number > 0),
  title VARCHAR(240) NOT NULL,
  lyrics TEXT NOT NULL,
  music_key VARCHAR(80),
  cross_reference_number INTEGER,
  notation_url VARCHAR(500),
  content_license VARCHAR(200) NOT NULL,
  license_reference VARCHAR(500) NOT NULL,
  verified BOOLEAN NOT NULL DEFAULT FALSE,
  demo BOOLEAN NOT NULL DEFAULT FALSE,
  updated_at TIMESTAMP NOT NULL,
  CONSTRAINT uq_hymns_version_number UNIQUE(version, number)
);
CREATE INDEX ix_hymns_verified_lookup ON hymns(version, verified, number);

CREATE TABLE admin_audit (
  id BIGSERIAL PRIMARY KEY,
  actor VARCHAR(160) NOT NULL,
  action VARCHAR(60) NOT NULL,
  entity_type VARCHAR(60) NOT NULL,
  entity_id VARCHAR(100),
  details TEXT,
  occurred_at TIMESTAMP NOT NULL
);
CREATE INDEX ix_admin_audit_time ON admin_audit(occurred_at DESC);
