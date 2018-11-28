package com.bank.transfer.service;

import com.bank.transfer.dto.DeactivatedAccount;
import com.bank.transfer.dto.QueryParams;
import com.bank.transfer.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    Account create(Account account);

    Account get(String accountId);

    List<Optional<Account>> get(List<String> accountId);

    List<Account> get(QueryParams params);

    Account changeBalance(Account account, BigDecimal difference);

    DeactivatedAccount delete(String accountId);
}
