package com.bank.transfer.test.util

import com.bank.transfer.dto.ClientAccount
import com.bank.transfer.dto.ClientAccountBalance
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import lombok.experimental.UtilityClass
import org.apache.commons.lang3.RandomUtils

import static com.bank.transfer.test.TestConst.ACCOUNTS_URL
import static io.micronaut.http.HttpRequest.POST

@UtilityClass
class AccountUtil {
    static void createAccountAndUpdateBalance(HttpClient httpClient, String accountId, String balance) {
        def account = new ClientAccount(accountId, RandomUtils.nextLong(0, 10000), null)
        httpClient.toBlocking()
                .retrieve(POST(ACCOUNTS_URL, account))
        def clientBalanceFrom = new ClientAccountBalance(accountId, balance)
        httpClient.toBlocking()
                .retrieve(HttpRequest.PUT(ACCOUNTS_URL + "/" + accountId, clientBalanceFrom))
    }

    static ClientAccount getAccount( HttpClient httpClient, String accountId) {
        httpClient.toBlocking()
                .retrieve(HttpRequest.GET(ACCOUNTS_URL + '/' + accountId), ClientAccount.class)
    }
}
