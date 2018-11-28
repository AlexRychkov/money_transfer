package com.bank.transfer.validator;

import com.bank.transfer.dto.ClientAccount;
import com.bank.transfer.dto.ClientAccountBalance;
import com.bank.transfer.entity.Account;
import com.bank.transfer.exception.ValidationException;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

import static com.bank.transfer.validator.NumericValidator.validateNumeric;

@UtilityClass
public class AccountValidator {
    public static void validateCreateRequest(ClientAccount clientAccount) {
        ObjectValidator.nonNull(clientAccount.getAccountId(), "accountId");
        ObjectValidator.test(() -> clientAccount.getCustomerId() != 0, "customerId");
    }

    public static void validateUpdateBalanceRequest(ClientAccountBalance clientAccountBalance) {
        ObjectValidator.nonNull(clientAccountBalance.getAmount(), "amount");
        validateNumeric(clientAccountBalance.getAmount(), "amount");
    }

    public static void validateBalanceNotEqualZero(Account account) {
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new ValidationException(String.format("Account \'%s\' balance not equal zero", account.getAccountId()));
        }
    }
}
