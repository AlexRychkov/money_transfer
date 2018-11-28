package com.bank.transfer.builder;

import com.bank.transfer.dto.*;
import com.google.common.collect.ImmutableMap;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.bank.transfer.dto.FilterOp.*;
import static com.bank.transfer.dto.FilterType.MONEY;
import static com.bank.transfer.dto.SortOrder.ASC;
import static com.bank.transfer.dto.SortOrder.DESC;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

@RunWith(DataProviderRunner.class)
public class SqlBuilderTest {
    @DataProvider
    public static Object[][] builderDataProvider() {
        return new Object[][]{
                {
                        "superTable",
                        new QueryParams(
                                newArrayList(new Sort("someField", ASC)),
                                new Pagination(100, 10000),
                                newArrayList(new Filter("balance", FilterOp.LT, "101"))
                        ),
                        "select * from superTable where balance < 101 order by someField asc limit 100 offset 10000"
                },
                {
                        "accounts",
                        new QueryParams(
                                newArrayList(
                                        new Sort("customerId", DESC),
                                        new Sort("accountId", ASC)
                                ),
                                new Pagination(10, 9),
                                newArrayList(
                                        new Filter("balance", GT, "101", MONEY),
                                        new Filter("balance", LTE, "200", MONEY)
                                )
                        ),
                        "select * from accounts where balance > '101'::money and balance <= '200'::money order by customerId desc, accountId asc limit 10 offset 9"
                },
                {
                        "transfer",
                        new QueryParams(
                                emptyList(),
                                new Pagination(),
                                newArrayList(new Filter("amount", EQ, "708"))
                        ),
                        "select * from transfer where amount = 708 limit 20"
                },
                {
                        "accounts",
                        new QueryParams(
                                newArrayList(
                                        new Sort("customerId", DESC),
                                        new Sort("accountId", ASC)
                                ),
                                new Pagination(),
                                emptyList()
                        ),
                        "select * from accounts order by customerId desc, accountId asc limit 20"
                },
                {
                        "arara",
                        new QueryParams(
                                emptyList(),
                                new Pagination(),
                                emptyList()
                        ),
                        "select * from arara limit 20"
                }
        };
    }

    @Test
    @UseDataProvider("builderDataProvider")
    public void testMapThrowValidationException(String tableName, QueryParams params, String expected) {
        val actual = SqlBuilder.of(tableName, params);
        assertEquals(expected, actual);
    }

    @Test
    public void testMapWithTypes() {
        val queryParams = new QueryParams(
                newArrayList(
                        new Sort("customerId", DESC),
                        new Sort("accountId", ASC)
                ),
                new Pagination(10, 9),
                newArrayList(
                        new Filter("balance", GT, "101"),
                        new Filter("balance", LTE, "200")
                )
        );
        val actual = SqlBuilder.query("accounts")
                .where(queryParams.getFilters(), ImmutableMap.of("balance", MONEY))
                .order(queryParams.getSorts())
                .pagination(new Pagination(10, 100));
        assertEquals(
                "select * from accounts where balance > '101'::money and balance <= '200'::money order by customerId desc, accountId asc limit 10 offset 100",
                actual
        );
    }
}