CREATE TABLE expenses (
            id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
            amount_cents    INTEGER NOT NULL CHECK(amount_cents BETWEEN 1 and 10000000),
            merchant        VARCHAR(100) NOT NULL CHECK (length(trim(merchant)) > 0),
            category        VARCHAR(50) NOT NULL CHECK(category IN ('FOOD', 'GROCERIES', 'TRANSPORTATION', 'HOUSING', 'BILLS', 'SHOPPING', 'FUN', 'HEALTH', 'OTHER')),
            spent_on        DATE NOT NULL,
            note            VARCHAR(500),
            created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_expenses_user_spent ON expenses (user_id, spent_on DESC);