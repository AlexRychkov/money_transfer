package com.bank.transfer.repository.impl;

import com.bank.transfer.builder.SqlBuilder;
import com.bank.transfer.dto.QueryParams;
import com.bank.transfer.entity.Account;
import com.bank.transfer.exception.EntityNotFoundException;
import com.bank.transfer.repository.AccountRepository;
import com.bank.transfer.repository.mapper.AccountRowMapper;
import com.bank.transfer.validator.AccountValidator;
import com.bank.transfer.validator.TransferValidator;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.bank.transfer.dto.FilterType.MONEY;
import static com.bank.transfer.dto.FilterType.STRING;
import static java.util.stream.Collectors.toList;
import static org.jdbi.v3.core.transaction.TransactionIsolationLevel.READ_COMMITTED;

@Slf4j
@Singleton
public class AccountRepositoryImpl implements AccountRepository {
    private Jdbi jdbi;
    private AccountRowMapper accountRowMapper;

    public AccountRepositoryImpl(Jdbi jdbi, AccountRowMapper accountRowMapper) {
        this.jdbi = jdbi;
        this.accountRowMapper = accountRowMapper;
    }

    @Override
    public void create(Account account) {
        jdbi.useHandle(handle -> handle
                .createUpdate("insert into accounts(accountId, customerId, balance) values (?, ?, 0)")
                .bind(0, account.getAccountId())
                .bind(1, account.getCustomerId())
                .execute()
        );
    }

    @Override
    public List<Account> read(QueryParams queryParams) {
        String query = SqlBuilder.query("accounts")
                .where(queryParams.getFilters(), ImmutableMap.of("balance", MONEY, "accountId", STRING))
                .order(queryParams.getSorts())
                .pagination(queryParams.getPagination());
        return jdbi.withHandle(handle -> handle
                .createQuery(query)
                .map(accountRowMapper)
                .list()
        );
    }

    @Override
    public Optional<Account> read(String accountId) {
        return jdbi.withHandle(handle -> read(accountId, handle));
    }

    private Optional<Account> read(String accountId, Handle handle) {
        return handle
                .createQuery("select * from accounts where accountId = ?")
                .bind(0, accountId)
                .map(accountRowMapper)
                .findFirst();
    }

    @Override
    public List<Optional<Account>> readList(List<String> accountIds) {
        List<Optional<Account>> accounts;
        try (Handle handle = jdbi.open()) {
            handle.setTransactionIsolation(READ_COMMITTED);
            handle.setReadOnly(true);
            handle.begin();
            accounts = accountIds.stream()
                    .map(id -> read(id, handle))
                    .collect(toList());
            handle.commit();
        }
        return accounts;
    }

    @Override
    public Account delete(String accountId) {
        return jdbi.inTransaction(handle -> {
            val maybeAccount = readWithLock(handle, accountId);
            val account = extractAccountOrElseThrowEntityNotFound(maybeAccount, accountId);
            AccountValidator.validateBalanceNotEqualZero(account);
            handle.createUpdate("delete from accounts where accountId = ?")
                    .bind(0, accountId)
                    .execute();
            return account;
        });
    }

    @Override
    public void updateBalance(Account account, BigDecimal difference) {
        jdbi.inTransaction(READ_COMMITTED, handle -> {
                    val maybeAccount = readWithLock(handle, account.getAccountId());
                    val actualAccount = extractAccountOrElseThrowEntityNotFound(maybeAccount, account.getAccountId());
                    if (!TransferValidator.validateAmount(actualAccount, difference)) {
                        TransferValidator.throwValidateFundsException(account.getAccountId());
                    }
                    return updateBalance(handle, account.getAccountId(), difference);
                }
        );
    }

    private Account extractAccountOrElseThrowEntityNotFound(Optional<Account> maybeAccount, String accountId) {
        return maybeAccount.orElseThrow(
                () -> new EntityNotFoundException("Account", accountId)
        );
    }

    @Override
    public void updateBalance(Handle handle, Account account, BigDecimal difference) {
        val maybeAccount = readWithLock(handle, account.getAccountId());
        val actualAccount = extractAccountOrElseThrowEntityNotFound(maybeAccount, account.getAccountId());
        if (!TransferValidator.validateAmount(actualAccount, difference)) {
            handle.rollback();
            TransferValidator.throwValidateFundsException(account.getAccountId());
        }
        updateBalance(handle, account.getAccountId(), difference);
    }

    private int updateBalance(Handle handle, String accountId, BigDecimal difference) {
        return handle.createUpdate("update accounts set balance = balance + ?::money where accountId = ?")
                .bind(0, difference)
                .bind(1, accountId)
                .execute();
    }

    private Optional<Account> readWithLock(Handle handle, String accountId) {
        return handle.createQuery("select * from accounts where accountId = ? for no key update")
                .bind(0, accountId)
                .map(accountRowMapper)
                .findFirst();
    }
}