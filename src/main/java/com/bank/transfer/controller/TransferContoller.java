package com.bank.transfer.controller;

import com.bank.transfer.dto.Transfer;
import com.bank.transfer.service.TransferService;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;

import java.math.BigDecimal;

import static com.bank.transfer.validator.TransferValidator.validateRequest;
import static io.micronaut.http.MediaType.APPLICATION_JSON;

@Controller("/transfers")
public class TransferContoller {
    private TransferService transferService;

    public TransferContoller(TransferService transferService) {
        this.transferService = transferService;
    }

    @Post(consumes = APPLICATION_JSON, processes = APPLICATION_JSON)
    public HttpStatus transfer(@Body Transfer transfer) {
        validateRequest(transfer);
        transferService.transfer(transfer.getFrom(), transfer.getTo(), new BigDecimal(transfer.getAmount()));
        return HttpStatus.OK;
    }


    @Get(value = "/{transferId}", processes = APPLICATION_JSON)
    public HttpResponse<String> getTransfer(String transferId) {
        return HttpResponse.notAllowed(HttpMethod.POST);
    }

    @Put(value = "/{transferId}", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public HttpResponse<String> updateTransfer(String transferId) {
        return HttpResponse.notAllowed(HttpMethod.POST);
    }

    @Delete(value = "/{transferId}", produces = APPLICATION_JSON)
    public HttpResponse<String> deleteTransfer(String transferId) {
        return HttpResponse.notAllowed(HttpMethod.POST);
    }
}
