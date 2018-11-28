package com.bank.transfer.controller;

import com.bank.transfer.dto.ClientAccount;
import com.bank.transfer.dto.ClientAccountBalance;
import com.bank.transfer.dto.DeactivatedAccount;
import com.bank.transfer.entity.Account;
import com.bank.transfer.mapper.QueryParamsMapper;
import com.bank.transfer.service.AccountService;
import com.bank.transfer.validator.AccountValidator;
import io.micronaut.http.HttpParameters;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import lombok.val;

import java.math.BigDecimal;
import java.util.List;

import static com.bank.transfer.mapper.AccountMapper.accountMapper;
import static io.micronaut.http.MediaType.APPLICATION_JSON;

@Controller("/accounts")
public class AccountController {
    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Get(value = "{?*}", produces = APPLICATION_JSON)
    public HttpResponse<List<ClientAccount>> getAccounts(HttpParameters queryParams) {
        val params = QueryParamsMapper.map(queryParams);
        val accounts = accountService.get(params);
        val clientAccounts = accountMapper.accountsToClientAccounts(accounts);
        return HttpResponse.ok(clientAccounts);
    }

    @Get(value = "/{accountId}", produces = APPLICATION_JSON)
    public HttpResponse<ClientAccount> getAccount(String accountId) {
        Account account = accountService.get(accountId);
        ClientAccount clientAccount = accountMapper.accountToClientAccount(account);
        return HttpResponse.ok(clientAccount);
    }

    @Post(consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public HttpResponse<ClientAccount> createAccount(@Body ClientAccount clientAccount) {
        AccountValidator.validateCreateRequest(clientAccount);
        Account account = accountMapper.clientAccountToAccount(clientAccount);
        account = accountService.create(account);
        ClientAccount created = accountMapper.accountToClientAccount(account);
        return HttpResponse.created(created);
    }

    @Put(value = "/{accountId}", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public HttpResponse<ClientAccount> updateClientAccountBalance(String accountId, @Body ClientAccountBalance clientAccountBalance) {
        AccountValidator.validateUpdateBalanceRequest(clientAccountBalance);
        clientAccountBalance.setAccountId(accountId);
        Account account = accountMapper.clientAccountBalanceToAccount(clientAccountBalance);
        account = accountService.changeBalance(account, new BigDecimal(clientAccountBalance.getAmount()));
        ClientAccount updated = accountMapper.accountToClientAccount(account);
        return HttpResponse.ok(updated);
    }

    @Delete(value = "/{accountId}", produces = APPLICATION_JSON)
    public HttpResponse<DeactivatedAccount> deleteClientAccount(String accountId) {
        DeactivatedAccount deactivatedAccount = accountService.delete(accountId);
        return HttpResponse.ok(deactivatedAccount);
    }
}




