CREATE TABLE payment_events
(
    id              BIGSERIAL PRIMARY KEY,
    payment_id      BIGINT                   REFERENCES payments (id) ON DELETE SET NULL,
    mercado_pago_id VARCHAR(100),
    received_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    event_type      VARCHAR(30)              NOT NULL,
    status_from_mp  VARCHAR(30),
    status_detail_from_mp  VARCHAR(50),
    error_message   TEXT
);

CREATE INDEX idx_pe_mp_id ON payment_events (mercado_pago_id);
CREATE INDEX idx_pe_received_at ON payment_events (received_at DESC);
CREATE INDEX idx_pe_payment_id ON payment_events (payment_id);