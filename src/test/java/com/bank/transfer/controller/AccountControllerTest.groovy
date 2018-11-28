package com.bank.transfer.controller


import com.bank.transfer.dto.ClientAccount
import com.bank.transfer.dto.ClientAccountBalance
import com.bank.transfer.test.util.AccountUtil
import io.micronaut.context.ApplicationContext
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import static com.bank.transfer.test.TestConst.ACCOUNTS_URL
import static io.micronaut.http.HttpRequest.*
import static io.micronaut.http.HttpStatus.*
import static junit.framework.Assert.assertEquals

class AccountControllerTest extends Specification {
    def NOT_EXIST_ACCOUNT_ID = "72634872638276"

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)

    @Shared
    @AutoCleanup
    HttpClient httpClient = HttpClient.create(embeddedServer.URL)

    void "GET return collection"() {
        given:
        AccountUtil.createAccountAndUpdateBalance(httpClient, "12414", "100")
        AccountUtil.createAccountAndUpdateBalance(httpClient, "36364", "3643.63")
        AccountUtil.createAccountAndUpdateBalance(httpClient, "23525", "4535436.63")

        when:
        def clients = httpClient.toBlocking()
                .exchange(GET(ACCOUNTS_URL + "?sort=customerId&accountId=lt:30000&limit=2&offset=0"), List)

        then:
        clients.status() == OK
        def accounts = clients.body()
        accounts.size() == 2
    }

    void "GET return 400 because of incorrect attribute"() {
        when:
        httpClient.toBlocking()
                .retrieve(GET(ACCOUNTS_URL + "?sort=asdfasdf"), List)

        then:
        def error = thrown(HttpClientResponseException)
        error.response.status == BAD_REQUEST
    }

    void "GET and POST should work together correctly"() {
        def ACCOUNT_ID = "2342"
        def CUSTOMER_ID = 36353

        given:
        def savedClientAccount = new ClientAccount(ACCOUNT_ID, CUSTOMER_ID, null)
        httpClient.toBlocking().retrieve(POST(ACCOUNTS_URL, savedClientAccount))

        when:
        def requestedClientAccount = httpClient.toBlocking()
                .exchange(GET(ACCOUNTS_URL + "/" + savedClientAccount.getAccountId()), ClientAccount)

        then:
        assertEquals(requestedClientAccount.status(), OK)
        def entity = requestedClientAccount.body()
        assertEquals(entity.getAccountId(), ACCOUNT_ID)
        assertEquals(entity.getCustomerId(), CUSTOMER_ID)
        assertEquals(entity.getBalance(), "0.00")
    }

    void "GET should return NOT_FOUND if Account not exist"() {
        when:
        httpClient.toBlocking().retrieve(GET(ACCOUNTS_URL + "/" + NOT_EXIST_ACCOUNT_ID))

        then:
        def error = thrown(HttpClientResponseException)
        error.response.status == NOT_FOUND
    }

    void "POST when create return 201"() {
        def ACCOUNT_ID = "73647"

        given:
        def clientAccount = new ClientAccount(ACCOUNT_ID, 387549, "3453.35")

        when:
        def posted = httpClient.toBlocking().exchange(POST(ACCOUNTS_URL, clientAccount))

        then:
        assertEquals(posted.status, CREATED)
    }

    void "POST ignore balance in request and not save it"() {
        def ACCOUNT_ID = "23452345"

        given:
        def savedClientAccount = new ClientAccount(ACCOUNT_ID, 387549, "3453.35")
        httpClient.toBlocking().retrieve(POST(ACCOUNTS_URL, savedClientAccount))

        when:
        def requestedClientAccount = httpClient.toBlocking()
                .exchange(GET(ACCOUNTS_URL + "/" + savedClientAccount.getAccountId()), ClientAccount)

        then:
        assertEquals(requestedClientAccount.body().getBalance(), "0.00")
    }

    void "POST return 409 if account already exist"() {
        def ACCOUNT_ID = "128147"

        given:
        def savedClientAccount = new ClientAccount(ACCOUNT_ID, 36273457, null)
        httpClient.toBlocking().retrieve(POST(ACCOUNTS_URL, savedClientAccount))

        when:
        httpClient.toBlocking().retrieve(POST(ACCOUNTS_URL, savedClientAccount))

        then:
        def error = thrown(HttpClientResponseException)
        error.response.status == CONFLICT
    }

    void "PUT should update Account and return 200"() {
        def ACCOUNT_ID = "234245"

        given:
        def clientAccount = new ClientAccount(ACCOUNT_ID, 387549, null)
        httpClient.toBlocking().exchange(POST(ACCOUNTS_URL, clientAccount))

        when:
        def clientAccountBalance = new ClientAccountBalance(ACCOUNT_ID, "1000")
        def updatedClientAccount = httpClient.toBlocking()
                .exchange(PUT(ACCOUNTS_URL + "/" + ACCOUNT_ID, clientAccountBalance), ClientAccount)

        then:
        assertEquals(updatedClientAccount.status(), OK)
        def entity = updatedClientAccount.body()
        assertEquals(entity.getAccountId(), ACCOUNT_ID)
        assertEquals(entity.getBalance(), "1000.00")
    }

    void "PUT should return 404 for non existing account"() {
        when:
        def clientAccountBalance = new ClientAccountBalance(NOT_EXIST_ACCOUNT_ID, "1000")
        httpClient.toBlocking()
                .retrieve(PUT(ACCOUNTS_URL + "/" + NOT_EXIST_ACCOUNT_ID, clientAccountBalance))

        then:
        def error = thrown(HttpClientResponseException)
        error.response.status == NOT_FOUND
    }

    void "PUT should return 400 if account have not funds"() {
        def ACCOUNT_ID = "83759354"

        given:
        def clientAccount = new ClientAccount(ACCOUNT_ID, 8297539, null)
        httpClient.toBlocking().exchange(POST(ACCOUNTS_URL, clientAccount))

        and:
        def clientAccountBalance = new ClientAccountBalance(ACCOUNT_ID, "1000")
        httpClient.toBlocking()
                .exchange(PUT(ACCOUNTS_URL + "/" + ACCOUNT_ID, clientAccountBalance), ClientAccount)

        when:
        clientAccountBalance = new ClientAccountBalance(ACCOUNT_ID, "-15000.00")
        httpClient.toBlocking()
                .retrieve(PUT(ACCOUNTS_URL + "/" + ACCOUNT_ID, clientAccountBalance))

        then:
        def error = thrown(HttpClientResponseException)
        error.response.status == BAD_REQUEST
    }

    void "PUT Account should update balance"() {
        def ACCOUNT_ID = "13414"
        def CUSTOMER_ID = 28347

        given:
        def clientAccount = new ClientAccount(ACCOUNT_ID, CUSTOMER_ID, null)
        httpClient.toBlocking().retrieve(POST(ACCOUNTS_URL, clientAccount))

        when:
        def clientBalanceFrom = new ClientAccountBalance(ACCOUNT_ID, "34533.45")
        def response = httpClient.toBlocking()
                .retrieve(PUT(ACCOUNTS_URL + "/" + ACCOUNT_ID, clientBalanceFrom), ClientAccount.class)

        then:
        assertEquals(response.getBalance(), "34533.45")
        assertEquals(response.getAccountId(), ACCOUNT_ID)
    }

    void "DELETE should delete and return 200"() {
        def ACCOUNT_ID = "83745399847"
        def CUSTOMER_ID = 367427357

        given:
        def clientAccount = new ClientAccount(ACCOUNT_ID, CUSTOMER_ID, null)
        httpClient.toBlocking().exchange(POST(ACCOUNTS_URL, clientAccount))

        when:
        def response = httpClient.toBlocking().exchange(DELETE(ACCOUNTS_URL + "/" + ACCOUNT_ID))

        then:
        response.status() == OK
    }

    void "DELETE should return 404 if account not exist"() {
        when:
        httpClient.toBlocking().retrieve(DELETE(ACCOUNTS_URL + "/" + NOT_EXIST_ACCOUNT_ID))

        then:
        def error = thrown(HttpClientResponseException)
        error.response.status == NOT_FOUND
    }
}