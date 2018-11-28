package com.bank.transfer.repository.impl;

import com.bank.transfer.entity.Account;
import com.bank.transfer.repository.AccountRepository;
import com.bank.transfer.repository.TransferRepository;
import com.bank.transfer.repository.mapper.AccountRowMapper;
import com.bank.transfer.test.repository.AbstractDatabaseTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class TransferRepositoryImplTest extends AbstractDatabaseTest {
    private TransferRepository transferRepository;
    private AccountRepository accountRepository;

    @Before
    public void before() throws IOException, URISyntaxException {
        super.before();
        AccountRowMapper accountRowMapper = new AccountRowMapper();
        accountRepository = new AccountRepositoryImpl(jdbi, accountRowMapper);
        transferRepository = new TransferRepositoryImpl(jdbi, accountRepository);
    }

    @Test
    public void testTransfer() {
        Account account1 = new Account("234", 35L, null);
        accountRepository.create(account1);
        accountRepository.updateBalance(account1, new BigDecimal("1000.5"));
        Account account2 = new Account("34534", 3535L, null);
        accountRepository.create(account2);
        accountRepository.updateBalance(account2, new BigDecimal("2000.5"));
        transferRepository.transfer(account1, account2, new BigDecimal("1000"));
        account1 = accountRepository.read(account1.getAccountId()).get();
        assertEquals(account1.getBalance(), new BigDecimal("0.50"));
        account2 = accountRepository.read(account2.getAccountId()).get();
        assertEquals(account2.getBalance(), new BigDecimal("3000.50"));
    }
}