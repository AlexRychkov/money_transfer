package com.bank.transfer.service.impl;

import com.bank.transfer.entity.Account;
import com.bank.transfer.repository.TransferRepository;
import com.bank.transfer.service.AccountService;
import com.bank.transfer.service.TransferService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.bank.transfer.validator.TransferValidator.validateTransferability;
import static java.util.Arrays.asList;

@Singleton
@Slf4j
public class TransferServiceImpl implements TransferService {
    private AccountService accountService;
    private TransferRepository transferRepository;

    public TransferServiceImpl(AccountService accountService, TransferRepository transferRepository) {
        this.accountService = accountService;
        this.transferRepository = transferRepository;
    }

    public void transfer(String fromAccount, String toAccount, BigDecimal amount) {
        List<Optional<Account>> accounts = accountService.get(asList(fromAccount, toAccount));
        validateTransferability(accounts, fromAccount, toAccount, amount);
        transferRepository.transfer(accounts.get(0).get(), accounts.get(1).get(), amount);
    }
}