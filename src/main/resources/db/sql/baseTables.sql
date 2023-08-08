--liquibase formatted sql
-- changeset stc:03082023

create table currency
(
    in_use boolean default false,
    name varchar,
    id     bigserial not null,
    code   varchar(255),
    primary key (id)
);
create table rates
(
    rate         float4,
    currency_id     bigint,
    id           bigserial not null,
    request_date date,
    primary key (id)
);
create table transactions
(
    amount      bigint,
    currency_id bigint,
    id          bigserial               not null,
    local_date  timestamp default now() not null,
    wallet_id   bigint,
    category    varchar(255) check (category in
                                    ('CATEGORY_INCOME_SALARY', 'CATEGORY_INCOME_GIFT', 'CATEGORY_INCOME_OTHER',
                                     'CATEGORY_OUTCOME_GROCERIES', 'CATEGORY_OUTCOME_TRAVEL', 'CATEGORY_OUTCOME_RENT',
                                     'CATEGORY_OUTCOME_MOVING', 'CATEGORY_OUTCOME_RESTAURANTS',
                                     'CATEGORY_OUTCOME_HEALTH')),
    type        varchar(255) check (type in ('TYPE_INCOME', 'TYPE_TRANSFER', 'TYPE_OUTCOME')),
    primary key (id)
);

create table wallet
(
    currency_id bigint,
    id          bigserial not null,
    type        varchar(255) check (type in ('WALLET_CASH', 'WALLET_ELECTRONIC')),
    primary key (id)
);

create table wallet_transactions
(
    transactions_id bigint not null unique,
    wallet_id       bigint not null
);

create table whitelist
(
    id          bigserial not null,
    telegram_id bigint,
    primary key (id)
);

alter table rates
    add constraint rates_currency_fk
        foreign key (currency_id)
            references currency;

alter table transactions
    add constraint transactions_currency_fk
        foreign key (currency_id)
            references currency;

alter table transactions
    add constraint transactions_wallet_fk
        foreign key (wallet_id)
            references wallet;

alter table wallet
    add constraint wallet_currency_fk
        foreign key (currency_id)
            references currency;

alter table wallet_transactions
    add constraint wallet_transactions_transactions_fk
        foreign key (transactions_id)
            references transactions;

alter table wallet_transactions
    add constraint wallet_transactions_wallet_fk
        foreign key (wallet_id)
            references wallet;

