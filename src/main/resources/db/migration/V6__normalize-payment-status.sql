UPDATE payments SET status = UPPER(status);

UPDATE payments SET status = 'APPROVED' WHERE status = 'PAGAMENTO REALIZADO';

ALTER TABLE payments ALTER COLUMN status TYPE VARCHAR(20);