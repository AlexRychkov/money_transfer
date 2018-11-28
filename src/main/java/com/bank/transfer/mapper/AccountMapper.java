package com.bank.transfer.mapper;

import com.bank.transfer.dto.ClientAccount;
import com.bank.transfer.dto.ClientAccountBalance;
import com.bank.transfer.dto.DeactivatedAccount;
import com.bank.transfer.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.List;

@Mapper
public interface AccountMapper {
    AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    Account clientAccountToAccount(ClientAccount clientAccount);

    @Mapping(target = "balance", constant = "0")
    Account clientAccountBalanceToAccount(ClientAccountBalance clientAccountBalance);

    ClientAccount accountToClientAccount(Account account);

    List<ClientAccount> accountsToClientAccounts(List<Account> account);

    @Mappings({
            @Mapping(target = "deactivateTime", source = "deactivateTime"),
            @Mapping(target = "accountId", source = "account.accountId"),
            @Mapping(target = "customerId", source = "account.customerId")
    })
    DeactivatedAccount accountToDeactivatedAccount(Account account, Date deactivateTime);
}