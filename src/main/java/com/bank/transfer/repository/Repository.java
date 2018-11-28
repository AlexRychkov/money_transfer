package com.bank.transfer.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, K> {
    void create(T account);

    Optional<T> read(K id);

    List<Optional<T>> readList(List<K> accountIds);

    T delete(K accountId);
}