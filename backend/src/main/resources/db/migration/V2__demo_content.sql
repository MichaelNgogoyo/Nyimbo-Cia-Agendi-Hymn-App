INSERT INTO events (title, description, category, event_date, start_time, end_time, venue, organizer, published, archived, demo, created_at, updated_at)
VALUES
('Sabbath worship', 'Sample event used to preview offline event listings, bookmarks and calendar actions. It is not a published church program.', 'Sabbath Worship', '2026-12-05', '09:00', '12:00', 'Demo venue — replace with verified venue', 'Demo organizer', TRUE, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Youth choir program', 'Sample event used to preview event search and reminder features. Replace it with a verified event before publication.', 'Choir Events', '2026-12-12', '15:00', '17:00', 'Demo venue — replace with verified venue', 'Demo organizer', TRUE, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO announcements (title, summary, body, published, archived, demo, published_at, created_at, updated_at)
VALUES ('DEMO • Church announcement', 'Sample announcement — replace before real use.', 'This is demonstration content to preview the administration and announcement delivery flow. It does not describe an actual church notice.', TRUE, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
