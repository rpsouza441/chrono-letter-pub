CREATE TABLE letters (
    id                      UUID PRIMARY KEY,
    user_id                 UUID NOT NULL,
    content                 TEXT NOT NULL,
    scheduled_date          DATE NOT NULL,
    timezone                VARCHAR(50) NOT NULL,
    send_at                 TIMESTAMPTZ,
    send_hour_local_used    SMALLINT,
    status                  VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    sent_at                 TIMESTAMPTZ,
    failure_reason          TEXT,
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL,
    
    CONSTRAINT chk_letter_status CHECK (status IN ('DRAFT', 'PENDING', 'SENDING', 'SENT', 'FAILED'))
);

CREATE INDEX idx_letters_user ON letters (user_id);
CREATE INDEX idx_letters_pending ON letters (scheduled_date) WHERE status = 'PENDING' AND send_at IS NULL;
CREATE INDEX idx_letters_ready ON letters (send_at) WHERE status = 'PENDING' AND send_at IS NOT NULL;