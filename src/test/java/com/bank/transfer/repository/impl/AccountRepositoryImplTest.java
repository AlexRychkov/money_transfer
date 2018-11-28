package com.bank.transfer.repository.impl;

import com.bank.transfer.dto.Filter;
import com.bank.transfer.dto.Pagination;
import com.bank.transfer.dto.QueryParams;
import com.bank.transfer.dto.Sort;
import com.bank.transfer.entity.Account;
import com.bank.transfer.exception.ValidationException;
import com.bank.transfer.repository.AccountRepository;
import com.bank.transfer.repository.mapper.AccountRowMapper;
import com.bank.transfer.test.repository.AbstractDatabaseTest;
import com.google.common.collect.Ordering;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Consumer;

import static com.bank.transfer.dto.FilterOp.LT;
import static com.bank.transfer.dto.SortOrder.DESC;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.*;

@RunWith(DataProviderRunner.class)
public class AccountRepositoryImplTest extends AbstractDatabaseTest {
    private AccountRepository accountRepository;
    private AccountRowMapper accountRowMapper;

    @Before
    public void before() throws IOException, URISyntaxException {
        super.before();
        accountRowMapper = new AccountRowMapper();
        accountRepository = new AccountRepositoryImpl(jdbi, accountRowMapper);
        for (int i = 100; i < 200; i++) {
            Account account = new Account(String.valueOf(i), 1000 - i, null);
            accountRepository.create(account);
            BigDecimal amount = i % 2 == 0 ? new BigDecimal("500") : new BigDecimal("3000");
            accountRepository.updateBalance(account, amount);
        }
    }

    @Test
    public void testCreateAndRead() {
        val ACCOUNT_ID = "14141";
        Account account = new Account(ACCOUNT_ID, nextLong(), BigDecimal.TEN);
        accountRepository.create(account);
        val create = accountRepository.read(ACCOUNT_ID).get();
        assertEquals(account.getAccountId(), create.getAccountId());
        assertEquals(account.getCustomerId(), create.getCustomerId());
        assertTrue(create.getBalance().compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    public void testCreateAndReadList() {
        val ACCOUNT_ID1 = "2351341";
        val ACCOUNT_ID2 = "14134634141";
        Account account1 = new Account(ACCOUNT_ID1, 123516L, BigDecimal.TEN);
        Account account2 = new Account(ACCOUNT_ID2, 345L, null);
        accountRepository.create(account1);
        accountRepository.create(account2);
        val created = accountRepository.readList(asList(ACCOUNT_ID1, ACCOUNT_ID2));
        account1.setBalance(new BigDecimal("0.00"));
        account2.setBalance(new BigDecimal("0.00"));
        assertThat(created.get(0).get(), samePropertyValuesAs(account1));
        assertThat(created.get(1).get(), samePropertyValuesAs(account2));
    }

    @Test
    public void testCreateAndReadListNotDependsOnDbOrder() {
        val ACCOUNT_ID1 = "2837298375";
        val ACCOUNT_ID2 = "236";
        Account account1 = new Account(ACCOUNT_ID1, nextLong(), null);
        Account account2 = new Account(ACCOUNT_ID2, nextLong(), null);
        accountRepository.create(account1);
        accountRepository.create(account2);
        val created = accountRepository.readList(asList(ACCOUNT_ID1, ACCOUNT_ID2));
        assertThat(created.get(0).get().getAccountId(), is(ACCOUNT_ID1));
        assertThat(created.get(1).get().getAccountId(), is(ACCOUNT_ID2));
    }

    @Test
    public void testCreateAndDelete() {
        val ACCOUNT_ID = "34636";
        Account account = new Account(ACCOUNT_ID, nextLong(), BigDecimal.TEN);
        accountRepository.create(account);
        val created = accountRepository.read(ACCOUNT_ID).get();
        assertNotNull(created);
        val deleted = accountRepository.delete(created.getAccountId());
        assertThat(created, samePropertyValuesAs(deleted));
    }

    @Test
    public void testCreateAndUpdateBalance() {
        val ACCOUNT_ID = "34645645";
        val difference = new BigDecimal("123456.78");
        Account account = new Account(ACCOUNT_ID, nextLong(), null);
        accountRepository.create(account);
        val created = accountRepository.read(ACCOUNT_ID).get();
        accountRepository.updateBalance(created, difference);
        val updated = accountRepository.read(created.getAccountId()).get();
        assertEquals(updated.getAccountId(), created.getAccountId());
        assertEquals(updated.getCustomerId(), created.getCustomerId());
        assertEquals(updated.getBalance(), created.getBalance().add(difference));
    }

    @Test(expected = ValidationException.class)
    public void testUpdateBalanceThrowFundsException() {
        val ACCOUNT_ID = "983679";
        val difference = new BigDecimal("-123456.78");
        Account account = new Account(ACCOUNT_ID, nextLong(), null);
        accountRepository.create(account);
        accountRepository.updateBalance(account, difference);
    }

    @DataProvider
    public static Object[][] builderDataProvider() {
        return new Object[][]{
                {
                        new QueryParams(
                                newArrayList(new Sort("customerId", DESC)),
                                new Pagination(),
                                emptyList()
                        ),
                        (Consumer<List<Account>>) (List<Account> accounts) -> assertTrue(
                                "Accounts should ordered by customerId desc",
                                Ordering.natural().reverse().isOrdered(accounts.stream().map(Account::getCustomerId).collect(toList()))
                        )
                },
                {
                        new QueryParams(
                                emptyList(),
                                new Pagination(),
                                newArrayList(new Filter("balance", LT, "1000"))
                        ),
                        (Consumer<List<Account>>) (List<Account> accounts) -> assertTrue(
                                "Accounts should not contain balance GT 1000",
                                accounts.stream().map(Account::getBalance).allMatch(balance -> balance.compareTo(BigDecimal.valueOf(1000)) < 0)
                        )
                },
                {
                        new QueryParams(
                                emptyList(),
                                new Pagination(),
                                newArrayList(new Filter("accountId", LT, "1000"))
                        ),
                        (Consumer<List<Account>>) (List<Account> accounts) -> assertThat(
                                accounts.size(), is(2)
                        )
                },
                {
                        new QueryParams(
                                emptyList(),
                                new Pagination(1, 0),
                                emptyList()
                        ),
                        (Consumer<List<Account>>) (List<Account> accounts) -> assertThat(
                                "Accounts should contain one element",
                                accounts.size(), is(1)
                        )
                },
                {
                        new QueryParams(
                                emptyList(),
                                new Pagination(10, 0),
                                emptyList()
                        ),
                        (Consumer<List<Account>>) (List<Account> accounts) -> assertThat(
                                accounts.size(), is(10)
                        )
                },
                {
                        new QueryParams(
                                emptyList(),
                                new Pagination(10, 10),
                                emptyList()
                        ),
                        (Consumer<List<Account>>) (List<Account> accounts) -> assertThat(
                                accounts.size(), is(10)
                        )
                },
                {
                        new QueryParams(
                                emptyList(),
                                new Pagination(10, 97),
                                emptyList()
                        ),
                        (Consumer<List<Account>>) (List<Account> accounts) -> assertThat(
                                accounts.size(), is(5)
                        )
                }
        };
    }

    @Test
    @UseDataProvider("builderDataProvider")
    public void testReadByParams(QueryParams queryParams, Consumer<List<Account>> assertFun) {
        List<Account> accounts = accountRepository.read(queryParams);
        assertFun.accept(accounts);
    }
}
