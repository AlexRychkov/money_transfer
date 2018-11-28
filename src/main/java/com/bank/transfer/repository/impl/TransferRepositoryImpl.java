package com.bank.transfer.repository.impl;

import com.bank.transfer.entity.Account;
import com.bank.transfer.repository.AccountRepository;
import com.bank.transfer.repository.TransferRepository;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import javax.inject.Singleton;
import java.math.BigDecimal;

import static org.jdbi.v3.core.transaction.TransactionIsolationLevel.READ_COMMITTED;

@Slf4j
@Singleton
public class TransferRepositoryImpl implements TransferRepository {
    private Jdbi jdbi;
    private AccountRepository accountRepository;

    public TransferRepositoryImpl(Jdbi jdbi, AccountRepository accountRepository) {
        this.jdbi = jdbi;
        this.accountRepository = accountRepository;
    }

    @Override
    public void transfer(Account from, Account to, BigDecimal amount) {
        if (from.getAccountId().equals(to.getAccountId())) {
            return;
        }
        Account minId;
        Account maxId;
        BigDecimal amountToMin;
        BigDecimal amountToMax;
        if (from.getAccountId().compareTo(to.getAccountId()) > 0) {
            minId = to;
            maxId = from;
            amountToMin = amount;
            amountToMax = amount.negate();
        } else {
            minId = from;
            maxId = to;
            amountToMin = amount.negate();
            amountToMax = amount;
        }
        try (Handle handle = jdbi.open()) {
            handle.setTransactionIsolation(READ_COMMITTED);
            handle.begin();
            accountRepository.updateBalance(handle, minId, amountToMin);
            accountRepository.updateBalance(handle, maxId, amountToMax);
            handle.commit();
        }
    }
}
