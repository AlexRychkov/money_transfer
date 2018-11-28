package com.bank.transfer.mapper;

import com.bank.transfer.dto.Filter;
import com.bank.transfer.dto.Pagination;
import com.bank.transfer.dto.QueryParams;
import com.bank.transfer.dto.Sort;
import com.bank.transfer.exception.ValidationException;
import com.google.common.collect.ImmutableMap;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import io.micronaut.core.convert.value.ConvertibleMultiValuesMap;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import static com.bank.transfer.dto.FilterOp.*;
import static com.bank.transfer.dto.SortOrder.ASC;
import static com.bank.transfer.dto.SortOrder.DESC;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.EMPTY_MAP;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

@RunWith(DataProviderRunner.class)
public class QueryParamsMapperTest {
    @DataProvider
    public static Object[][] mapDataProvider() {
        return new Object[][]{
                {
                        EMPTY_MAP,
                        new QueryParams(emptyList(), new Pagination(), emptyList())
                },
                {
                        ImmutableMap.of(
                                "sort", newArrayList("someField"),
                                "limit", newArrayList("100"),
                                "offset", newArrayList("10000"),
                                "balance", newArrayList("lt:101")
                        ),
                        new QueryParams(
                                newArrayList(new Sort("someField", ASC)),
                                new Pagination(100, 10000),
                                newArrayList(new Filter("balance", LT, "101"))
                        )
                },
                {
                        ImmutableMap.of(
                                "sort", newArrayList("desc(someField),asc(anotherField)"),
                                "balance", newArrayList("lt:101,gt:50")
                        ),
                        new QueryParams(
                                newArrayList(
                                        new Sort("someField", DESC),
                                        new Sort("anotherField", ASC)
                                ),
                                new Pagination(),
                                newArrayList(
                                        new Filter("balance", LT, "101"),
                                        new Filter("balance", GT, "50")
                                )
                        )
                },
                {
                        ImmutableMap.of(
                                "sort", newArrayList("desc(someField)", "anotherField"),
                                "offer", newArrayList("lte:200000", "gte:150000")
                        ),
                        new QueryParams(
                                newArrayList(
                                        new Sort("someField", DESC),
                                        new Sort("anotherField", ASC)
                                ),
                                new Pagination(),
                                newArrayList(
                                        new Filter("offer", LTE, "200000"),
                                        new Filter("offer", GTE, "150000")
                                )
                        )
                }
        };
    }

    @Test
    @UseDataProvider("mapDataProvider")
    public void testMap(Map<String, List<String>> paramsMap, QueryParams expected) {
        val httpParams = new ConvertibleMultiValuesMap(paramsMap);
        QueryParams params = QueryParamsMapper.map(httpParams);
        assertEquals(expected, params);
    }

    @DataProvider
    public static Object[][] mapIncorrectDataProvider() {
        return new Object[][]{
                {
                        ImmutableMap.of("limit", newArrayList("100,204"))
                },
                {
                        ImmutableMap.of("limit", newArrayList("100", "204"))
                },
                {
                        ImmutableMap.of("sort", newArrayList("desc(someField"))
                },
                {
                        ImmutableMap.of("filterAttr", newArrayList("something:200000"))
                },
                {
                        ImmutableMap.of("filterAt", newArrayList("bugaga"))
                }
        };
    }

    @Test(expected = ValidationException.class)
    @UseDataProvider("mapIncorrectDataProvider")
    public void testMapThrowValidationException(Map<String, List<String>> paramsMap) {
        val httpParams = new ConvertibleMultiValuesMap(paramsMap);
        QueryParamsMapper.map(httpParams);
    }
}