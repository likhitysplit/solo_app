CREATE TABLE budgets (
                        id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        category        VARCHAR(50) NOT NULL CHECK(category IN ('FOOD', 'GROCERIES', 'TRANSPORTATION', 'HOUSING', 'BILLS', 'SHOPPING', 'FUN', 'HEALTH', 'OTHER')),
                        period          VARCHAR(10) NOT NULL CHECK(period IN ('WEEKLY', 'MONTHLY')),
                        limit_cents     INTEGER NOT NULL CHECK(limit_cents BETWEEN 100 and 10000000),
                        effective_from  DATE NOT NULL,
                        created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                        UNIQUE (user_id, category, effective_from),
                        CHECK (period <> 'MONTHLY' OR EXTRACT(DAY FROM effective_from) = 1),
                        CHECK (period <> 'WEEKLY'  OR EXTRACT(ISODOW FROM effective_from) = 1)
);

