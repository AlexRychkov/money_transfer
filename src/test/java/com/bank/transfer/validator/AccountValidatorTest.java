package com.bank.transfer.validator;

import com.bank.transfer.dto.ClientAccount;
import com.bank.transfer.dto.ClientAccountBalance;
import com.bank.transfer.entity.Account;
import com.bank.transfer.exception.ValidationException;
import org.junit.Test;

import java.math.BigDecimal;

public class AccountValidatorTest {
    final Account ACCOUNT = Account.builder()
            .accountId("1")
            .customerId(1).build();

    @Test(expected = ValidationException.class)
    public void testValidateGTZeroBalance() {
        ACCOUNT.setBalance(BigDecimal.TEN);
        AccountValidator.validateBalanceNotEqualZero(ACCOUNT);
    }

    @Test
    public void testValidateGTZeroBalanceFromString() {
        ACCOUNT.setBalance(new BigDecimal("0.00"));
        AccountValidator.validateBalanceNotEqualZero(ACCOUNT);
    }

    @Test(expected = ValidationException.class)
    public void testValidateGTZero() {
        ACCOUNT.setBalance(new BigDecimal("-1110.00"));
        AccountValidator.validateBalanceNotEqualZero(ACCOUNT);
    }

    @Test
    public void testValidateUpdateBalanceRequestOK() {
        AccountValidator.validateUpdateBalanceRequest(new ClientAccountBalance(null, "2325.46"));
    }

    @Test(expected = ValidationException.class)
    public void testValidateUpdateBalanceRequestNotValidNum() {
        AccountValidator.validateUpdateBalanceRequest(new ClientAccountBalance(null, "534sdfsd.46"));
    }

    @Test(expected = ValidationException.class)
    public void testValidateUpdateBalanceRequestNotValidNumIsNull() {
        AccountValidator.validateUpdateBalanceRequest(new ClientAccountBalance("121414", null));
    }

    @Test
    public void testValidateCreateRequestOK() {
        AccountValidator.validateCreateRequest(new ClientAccount("345345", 345345L, "0"));
    }

    @Test(expected = ValidationException.class)
    public void testValidateCreateRequestNullAccountId() {
        AccountValidator.validateCreateRequest(new ClientAccount(null, 345345L, null));
    }

    @Test(expected = ValidationException.class)
    public void testValidateCreateRequestCustIdIsNull() {
        AccountValidator.validateCreateRequest(new ClientAccount("435345", 0, null));
    }
}