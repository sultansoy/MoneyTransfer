package com.sultansoy.app.transactions;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionsRepository {

    Map<String, Transaction> transactions = new HashMap<>();

    public Transaction create(Transaction transaction) {
        if (nonNull(transaction.getUuid())) {
            throw new TransactionException("New transaction uuid must be null! Uuid will be generated automatically");
        }

        if (transaction.getAmount() < 0) {
            throw new TransactionException("Invalid amount");
        }

        transaction.setUuid(UUID.randomUUID().toString());
        transactions.put(transaction.getUuid(), transaction);
        return copy(transaction);
    }

    public Transaction get(String uuid) {
        Transaction transaction = transactions.get(uuid);
        if (isNull(transaction)) {
            return null;
        }
        return copy(transaction);
    }

    public List<Transaction> getAll() {
        return getByPredicate(x -> true);
    }

    public List<Transaction> getTo(String uuid) {
        return getByPredicate(x -> Objects.equals(x.getToId(), uuid));
    }

    public List<Transaction> getFrom(String uuid) {
        return getByPredicate(x -> Objects.equals(x.getFromId(), uuid));
    }

    public List<Transaction> getFromTo(String fromUuid, String toUuid) {
        return getByPredicate(x -> Objects.equals(x.getToId(), toUuid) && Objects.equals(x.getFromId(), fromUuid));
    }

    private List<Transaction> getByPredicate(Predicate<Transaction> predicate) {
        return transactions.values()
                .stream()
                .filter(predicate)
                .map(this::copy)
                .sorted(Comparator.comparing(Transaction::getDate))
                .collect(Collectors.toList());
    }


    private Transaction copy(Transaction transaction) {
        Transaction copy = new Transaction();
        copy.setUuid(transaction.getUuid());
        copy.setFromId(transaction.getFromId());
        copy.setToId(transaction.getToId());
        copy.setAmount(transaction.getAmount());
        copy.setDate(transaction.getDate());
        return copy;
    }

}
