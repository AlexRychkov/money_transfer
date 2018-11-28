package com.bank.transfer.service.impl;

import com.bank.transfer.dto.DeactivatedAccount;
import com.bank.transfer.dto.QueryParams;
import com.bank.transfer.entity.Account;
import com.bank.transfer.exception.EntityNotFoundException;
import com.bank.transfer.repository.impl.AccountRepositoryImpl;
import com.bank.transfer.service.AccountService;
import lombok.val;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.bank.transfer.mapper.AccountMapper.accountMapper;

@Singleton
public class AccountServiceImpl implements AccountService {
    private AccountRepositoryImpl accountRepository;

    public AccountServiceImpl(AccountRepositoryImpl accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account create(Account account) {
        accountRepository.create(account);
        return get(account.getAccountId());
    }

    @Override
    public Account get(String accountId) {
        val maybeAccount = accountRepository.read(accountId);
        return maybeAccount.orElseThrow(
                () -> new EntityNotFoundException("Account", accountId)
        );
    }

    @Override
    public List<Optional<Account>> get(List<String> accountId) {
        return accountRepository.readList(accountId);
    }

    @Override
    public List<Account> get(QueryParams params) {
        return accountRepository.read(params);
    }

    @Override
    public Account changeBalance(Account account, BigDecimal difference) {
        accountRepository.updateBalance(account, difference);
        return accountRepository.read(account.getAccountId()).get();
    }

    @Override
    public DeactivatedAccount delete(String accountId) {
        Date now = new Date();
        Account account = accountRepository.delete(accountId);
        return accountMapper.accountToDeactivatedAccount(account, now);
    }
}
