package com.bank.transfer.validator;

import com.bank.transfer.dto.Transfer;
import com.bank.transfer.entity.Account;
import com.bank.transfer.exception.EntityNotFoundException;
import com.bank.transfer.exception.ValidationException;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

public class TransferValidatorTest {
    private Account account = new Account("234", 123, new BigDecimal("100"));
    private Transfer transfer = new Transfer("234345", "12367", "252525");

    @Test
    public void testValidateAmountOk() {
        assertTrue(TransferValidator.validateAmount(account, new BigDecimal("-99")));
    }

    @Test
    public void testValidateAmountOkPositiveNumber() {
        assertTrue(TransferValidator.validateAmount(account, new BigDecimal("10")));
    }

    @Test
    public void testValidateAmountBad() {
        assertTrue(TransferValidator.validateAmount(account, new BigDecimal("-100.00")));
    }

    @Test
    public void testValidateTransferOK() {
        transfer.setAmount("345345.45");
        TransferValidator.validateRequest(transfer);
    }

    @Test(expected = ValidationException.class)
    public void testValidateTransferNonNumeric() {
        transfer.setAmount("sdf.45");
        TransferValidator.validateRequest(transfer);
    }

    @Test(expected = NullPointerException.class)
    public void testValidateTransferabilityNullList() {
        TransferValidator.validateTransferability(null, "1", "3", BigDecimal.TEN);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testValidateTransferabilityEmptyList() {
        TransferValidator.validateTransferability(asList(Optional.empty(), Optional.empty()), "1", "3", BigDecimal.TEN);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testValidateTransferabilityOneElementList() {
        TransferValidator.validateTransferability(asList(Optional.of(new Account("1", 1, null)), Optional.empty()), "1", "3", BigDecimal.TEN);
    }

    @Test
    public void testValidateTransferability() {
        TransferValidator.validateTransferability(asList(Optional.of(new Account()), Optional.of(new Account())), "1", "3", BigDecimal.TEN);
    }

    @Test(expected = ValidationException.class)
    public void testThrowValidateFundsException() {
        TransferValidator.throwValidateFundsException("1");
    }
}