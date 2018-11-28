package com.bank.transfer.repository;

import com.bank.transfer.entity.Account;

import java.math.BigDecimal;

public interface TransferRepository {
    void transfer(Account from, Account to, BigDecimal amount);
}
