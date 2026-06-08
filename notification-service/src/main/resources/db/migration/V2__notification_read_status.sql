-- Notification read/unread state, category, and source-transaction (for de-duplication).
ALTER TABLE notifications ADD COLUMN type           VARCHAR(40);
ALTER TABLE notifications ADD COLUMN is_read        BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE notifications ADD COLUMN read_at        TIMESTAMP;
ALTER TABLE notifications ADD COLUMN transaction_id BIGINT;

ALTER TABLE notifications ALTER COLUMN message TYPE VARCHAR(500);

CREATE INDEX idx_notifications_user_read ON notifications (user_id, is_read);
CREATE INDEX idx_notifications_txn       ON notifications (transaction_id);
