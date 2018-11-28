create table accounts (
    accountId varchar(20) not null,
    customerId bigint not null,
    balance money not null,
    constraint accounts_pk primary key (accountId)
);

create index customer_idx on accounts (customerId);
create index balance_idx on accounts (balance);