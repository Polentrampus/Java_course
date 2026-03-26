create table if not exists money_transfers (
    id BIGINT primary key,
    id_account_from BIGINT not null REFERENCES accounts(id),
    id_account_to BIGINT not null REFERENCES accounts(id),
    summ decimal(19, 4) not null check (summ > 0),
    status VARCHAR(20) NOT NULL CHECK (status IN ('SUCCESS', 'FAILED')),
    created_at timestamp with time zone default now()
);