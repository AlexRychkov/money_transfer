package com.bank.transfer.repository;

import com.bank.transfer.dto.QueryParams;
import com.bank.transfer.entity.Account;
import org.jdbi.v3.core.Handle;

import java.math.BigDecimal;
import java.util.List;

public interface AccountRepository extends Repository<Account, String> {
    List<Account> read(QueryParams queryParams);

    void updateBalance(Account account, BigDecimal difference);

    void updateBalance(Handle handle, Account account, BigDecimal difference);
}