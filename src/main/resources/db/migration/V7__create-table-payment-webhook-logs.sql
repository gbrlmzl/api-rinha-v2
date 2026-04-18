CREATE TABLE payment_webhook_logs
(
    id                 BIGSERIAL PRIMARY KEY,
    payment_id         BIGINT                   REFERENCES payments (id) ON DELETE SET NULL,
    mercado_pago_id    VARCHAR(100),
    received_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    raw_payload        JSONB                    NOT NULL,
    processing_outcome VARCHAR(20)              NOT NULL,
    error_message      TEXT
);

CREATE INDEX idx_pwl_mp_id ON payment_webhook_logs (mercado_pago_id);
CREATE INDEX idx_pwl_received_at ON payment_webhook_logs (received_at DESC);