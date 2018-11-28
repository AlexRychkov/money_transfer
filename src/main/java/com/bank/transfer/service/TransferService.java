package com.bank.transfer.service;

import java.math.BigDecimal;

public interface TransferService {
    void transfer(String fromAccount, String toAccount, BigDecimal amount);
}
