package com.bank.transfer.repository.mapper;

import com.bank.transfer.entity.Account;
import com.bank.transfer.util.TypeUtil;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import javax.inject.Singleton;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class AccountRowMapper implements RowMapper<Account> {
    @Override
    public Account map(ResultSet rs, StatementContext ctx) throws SQLException {
        return Account.builder()
                .accountId(rs.getString("accountId"))
                .customerId(rs.getLong("customerId"))
                .balance(TypeUtil.convert(rs.getString("balance")))
                .build();
    }
}
