create table if not exists  accounts (
                          id BIGINT primary key,
                          current_balance DECIMAL(19, 2) not null check (current_balance >= 0)
);