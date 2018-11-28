package com.bank.transfer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeactivatedAccount {
    private String accountId;
    private long customerId;
    private Date deactivateTime;
}
