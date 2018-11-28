package com.bank.transfer.validator;

import com.bank.transfer.dto.Transfer;
import com.bank.transfer.entity.Account;
import com.bank.transfer.exception.EntityNotFoundException;
import com.bank.transfer.exception.ValidationException;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@UtilityClass
public class TransferValidator {
    public static void validateRequest(Transfer transfer) {
        ObjectValidator.nonNull(transfer.getFrom(), "from");
        ObjectValidator.nonNull(transfer.getTo(), "to");
        ObjectValidator.nonNull(transfer.getAmount(), "amount");
        NumericValidator.validateNumeric(transfer.getAmount(), "amount");
    }

    public static void validateTransferability(List<Optional<Account>> accounts, String fromAccount, String toAccount, BigDecimal amount) {
        if (!accounts.get(0).isPresent()) {
            throw new EntityNotFoundException("Account", fromAccount);
        }
        if (!accounts.get(1).isPresent()) {
            throw new EntityNotFoundException("Account", toAccount);
        }
    }

    public static boolean validateAmount(Account account, BigDecimal amount) {
        return account.getBalance().add(amount).compareTo(BigDecimal.ZERO) >= 0;
    }

    public void throwValidateFundsException(String accountId) {
        throw new ValidationException(format("Account \'%s\' has insufficiently funds", accountId));
    }
}
