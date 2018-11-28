package com.bank.transfer.controller


import com.bank.transfer.dto.Transfer
import com.bank.transfer.test.util.AccountUtil
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.IntStream

import static com.bank.transfer.test.TestConst.TRANSFERS_URL
import static io.micronaut.http.HttpRequest.POST
import static io.micronaut.http.HttpStatus.METHOD_NOT_ALLOWED
import static io.micronaut.http.HttpStatus.NOT_FOUND
import static java.util.stream.Collectors.toList
import static junit.framework.Assert.assertEquals

class TransferControllerTest extends Specification {
    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)

    @Shared
    @AutoCleanup
    HttpClient httpClient = HttpClient.create(embeddedServer.URL)

    void "POST transfer in one direction should correct withdraw through transactions and fail when insufficiently funds"() {
        def FROM_ID = "13414453"
        def TO_ID = "23423"

        given:
        AccountUtil.createAccountAndUpdateBalance(httpClient, FROM_ID, "100")
        AccountUtil.createAccountAndUpdateBalance(httpClient, TO_ID, "300")

        when:
        def transfer = Transfer.builder()
                .from(FROM_ID)
                .to(TO_ID)
                .amount('10').build()

        and:
        List<HttpResponse<Object>> responses = IntStream.range(0, 11)
                .parallel()
                .mapToObj { num -> httpClient.exchange(POST(TRANSFERS_URL, transfer), Object.class) }
                .map { resp -> assertEquals(resp.blockingFirst().status(), HttpStatus.OK) }
                .collect(toList())

        then:
        HttpClientResponseException e = thrown(HttpClientResponseException)
        e.status == HttpStatus.BAD_REQUEST

        and:
        def accountFrom = AccountUtil.getAccount(httpClient, FROM_ID)
        def accountTo = AccountUtil.getAccount(httpClient, TO_ID)
        "0.00" == accountFrom.getBalance()
        "400.00" == accountTo.getBalance()
    }

    void "POST one time two directional transfers is correct"() {
        def CONCURRENT_QUERIES_COUNT = 100
        def CONCURRENT_QUERIES_PER_ACCOUNT = CONCURRENT_QUERIES_COUNT / 2
        def FIRST_ACC = "78435678345"
        def SECOND_ACC = "375394"
        def FIRST_ACC_BALANCE = "2345345.95"
        def SECOND_ACC_BALANCE = "32500345.10"
        def FIRST_ACC_WITHDRAW = "100"
        def SECOND_ACC_WITHDRAW = "1000"

        given:
        AccountUtil.createAccountAndUpdateBalance(httpClient, FIRST_ACC, FIRST_ACC_BALANCE)
        AccountUtil.createAccountAndUpdateBalance(httpClient, SECOND_ACC, SECOND_ACC_BALANCE)
        def expectedFirstAccountBalance = FIRST_ACC_BALANCE.toBigDecimal() -
                CONCURRENT_QUERIES_PER_ACCOUNT * FIRST_ACC_WITHDRAW.toBigDecimal() +
                CONCURRENT_QUERIES_PER_ACCOUNT * SECOND_ACC_WITHDRAW.toBigDecimal()
        def expectedSecondAccountBalance = SECOND_ACC_BALANCE.toBigDecimal() +
                CONCURRENT_QUERIES_PER_ACCOUNT * FIRST_ACC_WITHDRAW.toBigDecimal() -
                CONCURRENT_QUERIES_PER_ACCOUNT * SECOND_ACC_WITHDRAW.toBigDecimal()

        and:
        def firstToSecondTransfer = Transfer.builder()
                .from(FIRST_ACC)
                .to(SECOND_ACC)
                .amount(FIRST_ACC_WITHDRAW).build()
        def secondToFirstTransfer = Transfer.builder()
                .from(SECOND_ACC)
                .to(FIRST_ACC)
                .amount(SECOND_ACC_WITHDRAW).build()

        when:
        def publishers = IntStream.range(0, CONCURRENT_QUERIES_COUNT).mapToObj { num ->
            if (num % 2 == 0) {
                httpClient.exchange(POST(TRANSFERS_URL, firstToSecondTransfer), Object.class)
            } else {
                httpClient.exchange(POST(TRANSFERS_URL, secondToFirstTransfer), Object.class)
            }
        }.collect(toList())
        def statuses = publishers.stream()
                .parallel()
                .map { publisher -> publisher.blockingFirst().status }
                .collect(toList())

        then:
        statuses.forEach { status -> status == HttpStatus.OK }

        and:
        def firstAccount =  AccountUtil.getAccount(httpClient, FIRST_ACC)
        def secondAccount = AccountUtil.getAccount(httpClient, SECOND_ACC)
        firstAccount.getBalance() == expectedFirstAccountBalance.toString()
        secondAccount.getBalance() == expectedSecondAccountBalance.toString()
    }

    void "POST transfer should return 404 if one or two accounts not exist"() {
        given:
        def transfer = Transfer.builder()
                .from("345345")
                .to("3784628")
                .amount('10').build()

        when:
        httpClient.toBlocking().retrieve(POST(TRANSFERS_URL, transfer))

        then:
        def error = thrown(HttpClientResponseException)
        error.response.status == NOT_FOUND
    }

    void "GET not allowed"() {
        when:
        httpClient.toBlocking().exchange(HttpRequest.GET(TRANSFERS_URL + "/234234242"), String)

        then:
        def error = thrown(HttpClientResponseException)
        error.response.status == METHOD_NOT_ALLOWED
    }
}