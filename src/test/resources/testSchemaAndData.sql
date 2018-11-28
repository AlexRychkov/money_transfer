create table accounts (
    accountId varchar(20) not null,
    customerId bigint not null,
    balance money not null,
    constraint accounts_pk primary key (accountId)
);

create index customer_idx on accounts (customerId);
create index balance_idx on accounts (balance);

insert into accounts (accountId, customerId, balance) values (1, 1, 100);
insert into accounts (accountId, customerId, balance) values (2, 2, 300);